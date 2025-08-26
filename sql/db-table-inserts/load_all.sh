#!/usr/bin/env bash
set -euo pipefail

# Connection params (export or pass inline)
# Example:
#   PGHOST=localhost PGPORT=5432 PGDATABASE=cmx PGUSER=postgres PGPASSWORD=secret ./load_all.sh
: "${PGHOST:?Set PGHOST}"
: "${PGPORT:=5432}"
: "${PGDATABASE:?Set PGDATABASE}"
: "${PGUSER:?Set PGUSER}"
: "${PGPASSWORD:?Set PGPASSWORD}"

PSQL="psql -h ${PGHOST} -p ${PGPORT} -U ${PGUSER} -d ${PGDATABASE} -v ON_ERROR_STOP=1"

# 0) Truncate (reset identities)
$PSQL -f 00_truncate.sql

# 1..8) Load seeds in FK-safe order
$PSQL -f 01_address.sql
$PSQL -f 02_vehicle.sql
$PSQL -f 03_insured.sql
$PSQL -f 04_policy.sql
$PSQL -f 05_surveyor.sql
$PSQL -f 06_fnol.sql
$PSQL -f 07_fnol_detail.sql
$PSQL -f 08_claim.sql

echo "Seed load complete."
