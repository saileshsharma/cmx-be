package com.cb.th.claims.cmx.service;


import com.cb.th.claims.cmx.entity.vehicle.Vehicle;

import java.util.List;

public interface VehicleService {
    Vehicle save(Vehicle entity);
    Vehicle findById(Long id);
    List<Vehicle> findAll();
    void delete(Long id);
}
