-- Master loader script (psql)
-- Usage: psql -h <host> -U <user> -d <db> -f master.sql
\set ON_ERROR_STOP on

-- 1) Clean slate
\i 00_truncate.sql

-- 2) Load in FK-safe order
\i 01_address.sql
\i 02_vehicle.sql
\i 03_insured.sql
\i 04_policy.sql
\i 05_surveyor.sql
\i 06_fnol.sql
\i 07_fnol_detail.sql
\i 08_claim.sql
