package com.cb.th.claims.cmx.exception;


import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final String code;     // e.g. "POLICY_NOT_ELIGIBLE"
    private final int httpStatus;  // e.g. 400 or 409

    public BusinessException(String code, String message, int httpStatus) {
        super(message);
        this.code = code;
        this.httpStatus = httpStatus;
    }
}

