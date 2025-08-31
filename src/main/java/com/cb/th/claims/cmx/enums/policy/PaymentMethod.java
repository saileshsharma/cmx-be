package com.cb.th.claims.cmx.enums.policy;


public enum PaymentMethod {
    CREDIT_CARD,
    DEBIT_CARD,
    BANK_TRANSFER,     // GIRO / ACH / EFT
    CHEQUE,
    CASH,
    DIGITAL_WALLET,    // e.g., PayNow, PromptPay, PayPal
    AUTO_DEBIT,        // standing instruction
    OTHER
}