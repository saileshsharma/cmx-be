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