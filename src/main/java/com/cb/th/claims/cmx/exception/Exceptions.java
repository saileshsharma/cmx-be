package com.cb.th.claims.cmx.exception;


public final class Exceptions {
    private Exceptions() {
    }

    public static BusinessException policyNotFound(String policyNumber) {
        return new BusinessException("POLICY_NOT_FOUND", "Policy not found: " + policyNumber, 404);
    }

    public static BusinessException vehicleNotFound(String reg) {
        return new BusinessException("VEHICLE_NOT_FOUND", "Vehicle not found for registration: " + reg, 404);
    }

    public static BusinessException addressNotFound(Long id) {
        return new BusinessException("ADDRESS_NOT_FOUND", "Address not found: " + id, 404);
    }
}