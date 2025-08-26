package com.cb.th.claims.cmx.service.impl;


import com.cb.th.claims.cmx.entity.vehicle.Vehicle;
import com.cb.th.claims.cmx.repository.vehicle.VehicleRepository;
import com.cb.th.claims.cmx.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {
    private final VehicleRepository vehicleRepository;

    @Override
    public Vehicle save(Vehicle entity) {
        return vehicleRepository.save(entity);
    }
    @Override
    public Vehicle findById(Long id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehicle not found with ID: " + id));
    }
    @Override
    public List<Vehicle> findAll() {
        return vehicleRepository.findAll();
    }
    @Override
    public void delete(Long id) {
        vehicleRepository.deleteById(id);
    }
}
