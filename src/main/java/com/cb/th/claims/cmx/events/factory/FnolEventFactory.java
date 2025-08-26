package com.cb.th.claims.cmx.events.factory;


import com.cb.th.claims.cmx.entity.address.Address;
import com.cb.th.claims.cmx.entity.fnol.FNOL;
import com.cb.th.claims.cmx.entity.policy.Policy;
import com.cb.th.claims.cmx.entity.vehicle.Vehicle;
import com.cb.th.claims.cmx.events.FnolCreated;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Locale;

@Component
public class FnolEventFactory {

    public FnolCreated create(FNOL fnol, Policy policy, Vehicle vehicle,
                              Address address, LocalDateTime accidentTs) {

        return FnolCreated.builder()
                .fnolId(String.valueOf(fnol.getId()))
                .policyNumber(policy.getPolicyNumber())
                .registrationNumber(vehicle.getRegistrationNumber())
                .regionId(deriveRegionId(address))
                .requiredSkills(deriveRequiredSkills(policy, vehicle))
                .accident(FnolCreated.Accident.builder()
                        .lat(safe(address.getLatitude()))
                        .lng(safe(address.getLongitude()))
                        .time(accidentTs.toInstant(ZoneOffset.UTC))
                        .severity(String.valueOf(fnol.getSeverity()))
                        .build())
                .meta(new FnolCreated.Meta("trace-" + fnol.getId()))
                .build();
    }

    private static double safe(Double v) {
        return v == null ? 0.0 : v;
    }

    /**
     * Simple region heuristic — adapt to your real mapping
     */
    private static String deriveRegionId(Address a) {
        final String city = a != null && a.getCity() != null ? a.getCity().toUpperCase(Locale.ROOT) : "";
        if (city.contains("QUEENSTOWN") || city.contains("CLEMENTI") || city.contains("BUKIT")) return "SG-CENTRAL";
        if (city.contains("BEDOK") || city.contains("TAMPINES") || city.contains("PASIR")) return "SG-EAST";
        if (city.contains("JURONG") || city.contains("TUAS")) return "SG-WEST";
        return "SG-CENTRAL";
    }

    /**
     * Minimal rule: motor lines need MOTOR skill
     */
    private static List<String> deriveRequiredSkills(Policy policy, Vehicle vehicle) {
        return List.of("MOTOR");
    }
}