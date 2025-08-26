package com.cb.th.claims.cmx.payload;

import com.cb.th.claims.cmx.entity.claim.Claim;
import com.cb.th.claims.cmx.entity.fnol.FNOL;

public class CreateFnolPayload {
    private final FNOL fnol;
    private final Claim claim;

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