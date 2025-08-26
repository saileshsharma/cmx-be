package com.cb.th.claims.cmx.enums.fnol;

public enum FNOLState {
    DRAFT("UI Saved"), SUBMITTED("User submits, minimal validation passed"), ENRICHING("System enriches (policy lookup, coverage, fraud signals)"), VALIDATED("Eligibility Confirmed; claim number reserved"), REJECTED("Invalid policy / out of coverage / duplicate / fraud");

    private final String description;

    FNOLState(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}