-- Truncate all tables and reset identities (safe for FK graphs)
BEGIN;
TRUNCATE TABLE
    claim,
    fnol_detail,
    fnol,
    policy,
    insured,
    vehicle,
    surveyor,
    address
RESTART IDENTITY CASCADE;
COMMIT;
