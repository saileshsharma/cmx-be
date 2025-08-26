-- master_seed.sql
-- Run after TRUNCATE ... RESTART IDENTITY CASCADE;
\i address_inserts.sql
\i vehicle_inserts.sql
\i insured_inserts.sql
\i policy_inserts.sql
\i surveyor_inserts.sql
\i fnol_inserts.sql
\i fnol_detail_inserts.sql
\i claim_inserts.sql
\i surveyor_assign_claims.sql