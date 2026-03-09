INSERT INTO sys_user (id, username, password, nickname, status, created_at, updated_at)
VALUES (1, 'admin', '{noop}admin123', 'System Admin', 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE updated_at = NOW();

INSERT INTO sys_role (id, role_code, role_name, status, created_at, updated_at)
VALUES (1, 'ADMIN', 'Administrator', 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE updated_at = NOW();

INSERT INTO sys_permission (id, permission_code, permission_name, status, created_at, updated_at)
VALUES
    (1, 'user:read', 'Read User', 1, NOW(), NOW()),
    (2, 'user:write', 'Write User', 1, NOW(), NOW()),
    (3, 'role:read', 'Read Role', 1, NOW(), NOW()),
    (4, 'role:write', 'Write Role', 1, NOW(), NOW()),
    (5, 'perm:read', 'Read Permission', 1, NOW(), NOW()),
    (6, 'perm:write', 'Write Permission', 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE updated_at = NOW();

INSERT INTO sys_user_role (user_id, role_id)
VALUES (1, 1)
ON DUPLICATE KEY UPDATE role_id = VALUES(role_id);

INSERT INTO sys_role_permission (role_id, permission_id)
VALUES
    (1, 1),
    (1, 2),
    (1, 3),
    (1, 4),
    (1, 5),
    (1, 6)
ON DUPLICATE KEY UPDATE permission_id = VALUES(permission_id);

