-- INSURED
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name='INSURED' AND p.name IN ('POLICY_READ_OWN','FNOL_CREATE','FNOL_READ','CLAIM_READ');

-- CALL_CENTER_AGENT
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name='CALL_CENTER_AGENT' AND p.name IN ('FNOL_CREATE','FNOL_READ','POLICY_READ','CLAIM_READ');

-- SURVEYOR_DISPATCHER
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name='SURVEYOR_DISPATCHER' AND p.name IN ('FNOL_READ','SURVEYOR_ASSIGN');

-- LOSS_ADJUSTER_SENIOR
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name='LOSS_ADJUSTER_SENIOR' AND p.name IN ('FNOL_READ','FNOL_UPDATE','CLAIM_READ','CLAIM_WRITE');

-- CLAIMS_PAYMENT_OFFICER
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name='CLAIMS_PAYMENT_OFFICER' AND p.name IN ('CLAIM_READ','CLAIM_WRITE');

-- SYS_ADMIN gets everything
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r CROSS JOIN permissions p
WHERE r.name='SYS_ADMIN';
