BEGIN;

-- Optional: snapshot before changes (drop after verifying)
-- CREATE TABLE policy_backup_yyMMdd AS SELECT * FROM policy;

WITH candidates AS (
  SELECT p.id,
         UPPER(COALESCE(p.lob, 'MOTOR')) AS lob,
         p.sum_insured,
         p.premium,

         /* Sum insured ranges by LOB */
         CASE
           WHEN UPPER(COALESCE(p.lob,'')) = 'MOTOR'    THEN 20000  + (random() * 180000)   -- 20k–200k
           WHEN UPPER(COALESCE(p.lob,'')) = 'PROPERTY' THEN 100000 + (random() * 1900000)  -- 100k–2.0m
           WHEN UPPER(COALESCE(p.lob,'')) = 'HEALTH'   THEN 50000  + (random() * 450000)   -- 50k–500k
           ELSE 75000 + (random() * 425000)                                        -- 75k–500k
         END AS si_new,

         /* Premium rate bands by LOB */
         CASE
           WHEN UPPER(COALESCE(p.lob,'')) = 'MOTOR'    THEN 0.020 + (random() * 0.025)  -- 2.0–4.5%
           WHEN UPPER(COALESCE(p.lob,'')) = 'PROPERTY' THEN 0.006 + (random() * 0.009)  -- 0.6–1.5%
           WHEN UPPER(COALESCE(p.lob,'')) = 'HEALTH'   THEN 0.030 + (random() * 0.040)  -- 3.0–7.0%
           ELSE 0.010 + (random() * 0.020)                                       -- 1.0–3.0%
         END AS rate
  FROM policy p
  WHERE (p.sum_insured IS NULL OR p.sum_insured = 0)
     OR (p.premium     IS NULL OR p.premium     = 0)
),
calc AS (
  SELECT c.id,
         /* If SI missing/zero, use si_new; else keep existing */
         CASE WHEN (p.sum_insured IS NULL OR p.sum_insured = 0)
              THEN c.si_new ELSE p.sum_insured END AS target_si,
         /* Rate per row from above */
         c.rate,
         /* Whether premium needs fill */
         (p.premium IS NULL OR p.premium = 0) AS needs_premium,
         (p.sum_insured IS NULL OR p.sum_insured = 0) AS needs_si
  FROM candidates c
  JOIN policy p USING (id)
)
UPDATE policy p
SET
  sum_insured = CASE
                  WHEN calc.needs_si
                  THEN ROUND(calc.target_si::numeric, 2)::double precision
                  ELSE p.sum_insured
                END,
  premium = CASE
              WHEN calc.needs_premium
              THEN ROUND((calc.target_si * calc.rate)::numeric, 2)::double precision
              ELSE p.premium
            END
FROM calc
WHERE p.id = calc.id;

COMMIT;















































































































































-- Change CHAR(64) -> VARCHAR(64)
ALTER TABLE image_asset
  ALTER COLUMN checksum_sha256 TYPE varchar(64)
  USING trim(trailing FROM checksum_sha256)::varchar(64);





UPDATE vehicle
SET chassis = (
    LPAD(
        CAST(FLOOR(RANDOM() * 1e15) AS BIGINT)::TEXT,
        15,
        '0'
    )
);


select fnol_reference_no, fnol_state, severity from fnol where fnol_reference_no = 'CHU25081800031';

SELECT FROM fnol WHERE fnol_reference_no = 'CHU25081700030';



SELECT *
FROM address
WHERE id = 1040;
-- (or if your table is named address)
-- SELECT * FROM address WHERE id = 1039;



SELECT *
FROM address a
WHERE EXISTS (
  SELECT 1
  FROM jsonb_each_text(to_jsonb(a)) kv
  WHERE kv.value ILIKE '%capita%'
);



SELECT *
FROM fnol f
WHERE EXISTS (
  SELECT 1
  FROM jsonb_each_text(to_jsonb(f)) kv
  WHERE kv.value ILIKE '%capita%'
);







UPDATE policy
SET end_date = make_date((extract(year FROM current_date)::int) + 1, 12, 31)
WHERE policy_status = 'BIND';










UPDATE vehicle
SET registration_number =
    (
      -- 4 uppercase letters
      (
        chr(65 + floor(random() * 26)::int) ||
        chr(65 + floor(random() * 26)::int) ||
        chr(65 + floor(random() * 26)::int) ||
        chr(65 + floor(random() * 26)::int)
      )
      ||
      -- 6 digits, left-padded with zeros
      lpad(floor(random() * 1000000)::text, 6, '0')
    );




SELECT id, registration_number
FROM vehicle
WHERE id = 545;


SELECT p.id AS policy_id, p.vehicle_id, v.registration_number
FROM policy p
LEFT JOIN vehicle v ON v.id = p.vehicle_id, p.policy_number
WHERE p.id = 545 OR p.policy_number = 'THAUTO0000545';







CREATE SEQUENCE IF NOT EXISTS fnol_index_addr_loc_id;
CREATE UNIQUE INDEX IF NOT EXISTS ux_fnol_reference_no ON fnol (fnol_reference_no);


SELECT setval('fnol_index_addr_loc_id', COALESCE((SELECT MAX(id) FROM fnol), 0) + 1, false);

---------------------------------------------------------------------------------------------------











-- (Optional) See what's there first
SELECT DISTINCT status FROM surveyor ORDER BY status;
SELECT DISTINCT surveyor_job_status FROM surveyor ORDER BY surveyor_job_status;

-- 1) Normalize availability status to your final set
UPDATE surveyor
SET status = UPPER(TRIM(status))
WHERE status IS NOT NULL;

-- Map legacy/other values to UNAVAILABLE
UPDATE surveyor
SET status = 'UNAVAILABLE'
WHERE status IS NULL
   OR status NOT IN ('AVAILABLE','UNAVAILABLE','INACTIVE')
   OR status IN ('BUSY','OFFLINE','IDLE','AWAY','BREAK','WORKING','PENDING','ON_THE_WAY','CLOSED');

-- 2) Canonicalize job status strings (keeps enum-friendly display)
UPDATE surveyor
SET surveyor_job_status = CASE
  WHEN surveyor_job_status IS NULL THEN NULL
  WHEN UPPER(REPLACE(REPLACE(surveyor_job_status,'-','_'),' ','_')) = 'ON_THE_WAY' THEN 'On_The_Way'
  WHEN UPPER(surveyor_job_status) = 'ACCEPTED' THEN 'Accepted'
  WHEN UPPER(surveyor_job_status) = 'WORKING'  THEN 'Working'
  WHEN UPPER(surveyor_job_status) = 'PENDING'  THEN 'Pending'
  WHEN UPPER(surveyor_job_status) = 'CLOSED'   THEN 'Closed'
  ELSE NULL
END;

-- 3) If AVAILABLE ⇒ no active job
UPDATE surveyor
SET surveyor_job_status = NULL,
    active_jobs_count  = 0
WHERE status = 'AVAILABLE'
  AND (surveyor_job_status IS NOT NULL OR COALESCE(active_jobs_count,0) <> 0);

-- 4) If there IS an active job ⇒ must be UNAVAILABLE
UPDATE surveyor
SET status = 'UNAVAILABLE'
WHERE UPPER(REPLACE(REPLACE(COALESCE(surveyor_job_status,''),'-','_'),' ','_'))
      IN ('ACCEPTED','WORKING','ON_THE_WAY','PENDING')
  AND status <> 'UNAVAILABLE';

-- 5) (Optional) If no active job and status is UNAVAILABLE, you can mark them AVAILABLE
--    Comment this out if you prefer to keep them UNAVAILABLE until manual review.
UPDATE surveyor
SET status = 'AVAILABLE'
WHERE status = 'UNAVAILABLE'
  AND (surveyor_job_status IS NULL OR UPPER(surveyor_job_status) = 'CLOSED')
  AND COALESCE(active_jobs_count,0) = 0;

-- 6) Quick sanity checks
SELECT
  status,
  COUNT(*) AS cnt,
  SUM(CASE WHEN status='AVAILABLE' AND (surveyor_job_status IS NOT NULL OR COALESCE(active_jobs_count,0)<>0) THEN 1 ELSE 0 END) AS available_with_job_violations,
  SUM(CASE WHEN status<>'UNAVAILABLE' AND UPPER(REPLACE(REPLACE(COALESCE(surveyor_job_status,''),'-','_'),' ','_')) IN ('ACCEPTED','WORKING','ON_THE_WAY','PENDING') THEN 1 ELSE 0 END) AS active_job_wrong_status
FROM surveyor
GROUP BY status
ORDER BY status;


----------------------------------

SELECT DISTINCT status FROM surveyor ORDER BY status;


----------------------------------------


-- 1) Trim + uppercase everything for consistent matching
UPDATE surveyor
SET status = UPPER(TRIM(status))
WHERE status IS NOT NULL;

-- 2) Map legacy / wrong values to your new tri-state
--    AVAILABLE stays AVAILABLE
--    BUSY, OFFLINE, ON_THE_WAY, PENDING, WORKING, CLOSED, etc. -> UNAVAILABLE
UPDATE surveyor SET status = 'UNAVAILABLE'
WHERE status IN ('BUSY','OFFLINE','ON_THE_WAY','PENDING','WORKING','CLOSED','IDLE','AWAY','BREAK');

-- 3) Anything else that’s not in the new set becomes UNAVAILABLE
UPDATE surveyor SET status = 'UNAVAILABLE'
WHERE status IS NULL OR status NOT IN ('AVAILABLE','UNAVAILABLE','INACTIVE');

-- 4) Keep is_active consistent (optional but recommended)
--    INACTIVE => is_active = false; others -> true if null
UPDATE surveyor SET is_active = FALSE
WHERE status = 'INACTIVE';

UPDATE surveyor SET is_active = COALESCE(is_active, TRUE)
WHERE status IN ('AVAILABLE','UNAVAILABLE');

----------------------------------------



UPDATE surveyor
SET is_active = false
WHERE id IN (
    SELECT id
    FROM surveyor
    ORDER BY id ASC    
);




UPDATE surveyor
SET internal = TRUE
WHERE id IN (
    SELECT id
    FROM surveyor
    ORDER BY id ASC
    LIMIT 25
);


UPDATE surveyor
SET internal = false
WHERE id IN (
    SELECT id
    FROM surveyor
    ORDER BY random()
    LIMIT (SELECT COUNT(*) / 2 FROM surveyor)
);

-- Optional: Verify counts after update
SELECT 
    SUM(CASE WHEN internal THEN 1 ELSE 0 END) AS internal_count,
    SUM(CASE WHEN NOT internal THEN 1 ELSE 0 END) AS external_count
FROM surveyor;






SELECT setval('address_id_seq', (SELECT MAX(id) FROM address) + 1);





-- one-time
CREATE SEQUENCE IF NOT EXISTS fnol_ref_seq;
CREATE UNIQUE INDEX IF NOT EXISTS ux_fnol_reference_no ON fnol (fnol_reference_no);


SELECT setval('fnol_id_seq', COALESCE((SELECT MAX(id) FROM fnol), 0) + 1, false);


UPDATE policy
SET policy_number = REPLACE(policy_number, '-', '');

SET claim_number = REPLACE(claim_number, '-', '');




BEGIN;

WITH to_delete AS (
  SELECT id
  FROM surveyor
  ORDER BY id
  OFFSET 250     -- keeps 1–250
  LIMIT 750      -- deletes 251–1000
)
DELETE FROM surveyor s
USING to_delete d
WHERE s.id = d.id;

-- Reset the serial/identity sequence to MAX(id)
SELECT setval(
  pg_get_serial_sequence('surveyor','id'),
  COALESCE(MAX(id), 0)
)
FROM surveyor;

COMMIT;
