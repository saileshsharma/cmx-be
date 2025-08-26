package com.cb.th.claims.cmx.service.fnol;

import com.cb.th.claims.cmx.entity.address.Address;
import com.cb.th.claims.cmx.entity.claim.Claim;
import com.cb.th.claims.cmx.entity.fnol.FNOL;
import com.cb.th.claims.cmx.entity.policy.Policy;
import com.cb.th.claims.cmx.entity.vehicle.Vehicle;
import com.cb.th.claims.cmx.enums.claim.ClaimSeverity;
import com.cb.th.claims.cmx.events.FnolCreated;
import com.cb.th.claims.cmx.events.factory.FnolEventFactory;
import com.cb.th.claims.cmx.exception.Exceptions;
import com.cb.th.claims.cmx.kafka.FnolEventPublisher;
import com.cb.th.claims.cmx.repository.address.AddressRepository;
import com.cb.th.claims.cmx.repository.fnol.FNOLRepository;
import com.cb.th.claims.cmx.repository.policy.PolicyRepository;
import com.cb.th.claims.cmx.repository.vehicle.VehicleRepository;
import com.cb.th.claims.cmx.service.domain.FnolDomainValidator;
import com.cb.th.claims.cmx.util.RegistrationNormalizer;
import com.cb.th.claims.cmx.util.TimeParsers;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@Validated
@RequiredArgsConstructor
public class FnolService {

    // Repositories & collaborators
    private final FNOLRepository fnolRepository;
    private final PolicyRepository policyRepository;
    private final VehicleRepository vehicleRepository;
    private final AddressRepository addressRepository;

    private final FnolReferenceGenerator referenceGenerator;
    private final FnolEventPublisher eventPublisher;

    private final FnolDomainValidator validator;
    private final FnolEventFactory eventFactory;

    // ==== DTOs / Payloads ====

    public record CreateFnolCommand(@NotBlank String policyNumber, @NotBlank String registrationNumber,
                                    @NotNull Long accidentLocationId, @NotBlank String description,
                                    @NotNull ClaimSeverity severity, @NotBlank String accidentDate) {
    }

    public static class CreateFnolPayload {
        private final FNOL fnol;
        private final Claim claim; // reserved for future linkage

        public CreateFnolPayload(FNOL fnol, Claim claim) {
            this.fnol = fnol;
            this.claim = claim;
        }

        public FNOL getFnol() {
            return fnol;
        }

        public Claim getClaim() {
            return claim;
        }
    }

    // ==== Entry point ====

    @Transactional
    public CreateFnolPayload createFnol(@Valid @NotNull CreateFnolCommand cmd) {
        // Parse & lookups
        final LocalDateTime accidentTs = TimeParsers.parseAccidentDateOrThrow(cmd.accidentDate());
        final LocalDate accidentDate = accidentTs.toLocalDate();

        final Policy policy = policyRepository.findByPolicyNumber(cmd.policyNumber()).orElseThrow(() -> Exceptions.policyNotFound(cmd.policyNumber()));
        final Address address = addressRepository.findById(cmd.accidentLocationId()).orElseThrow(() -> Exceptions.addressNotFound(cmd.accidentLocationId()));

        final String normalizedReg = RegistrationNormalizer.normalize(cmd.registrationNumber());
        final Vehicle vehicle = vehicleRepository.findByRegistrationNormalized(normalizedReg).orElseThrow(() -> Exceptions.vehicleNotFound(normalizedReg));

        // Domain rules
        validator.validatePolicyEligibilityOrThrow(policy, accidentDate);
        validator.validateVehicleBelongsToPolicyOrThrow(vehicle, policy);
        validator.validateNoDuplicateOrOpenFnolOrThrow(policy, normalizedReg, accidentTs);

        // Assemble & persist
        final FNOL toSave = assembleFnol(policy, vehicle, address, cmd, accidentTs);
        final FNOL saved = fnolRepository.save(toSave);

        // Publish after commit (Kafka guarded by publisher/outbox)
        publishAfterCommit(saved, policy, vehicle, address, accidentTs);

        return new CreateFnolPayload(saved, null);
    }

    private FNOL assembleFnol(Policy policy, Vehicle vehicle, Address address, CreateFnolCommand cmd, LocalDateTime accidentTs) {
        var fnol = new FNOL();
        fnol.setFnolState(com.cb.th.claims.cmx.enums.fnol.FNOLState.DRAFT);
        fnol.setFnolReferenceNo(referenceGenerator.next());
        fnol.setPolicy(policy);
        fnol.setVehicle(vehicle);
        fnol.setAccidentLocation(address);
        fnol.setDescription(cmd.description());
        fnol.setSeverity(cmd.severity());
        fnol.setAccidentDate(accidentTs);
        return fnol;
    }

    private void publishAfterCommit(FNOL fnol, Policy policy, Vehicle vehicle, Address address, LocalDateTime accidentTs) {
        final FnolCreated evt = eventFactory.create(fnol, policy, vehicle, address, accidentTs);
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                eventPublisher.publishCreated(evt);
            }
        });
    }
}
