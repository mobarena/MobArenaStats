INSERT INTO schema_migrations (
  filename,
  executed,
  success,
  error
) VALUES (
  :filename,
  :executed,
  :success,
  :error
) ON DUPLICATE KEY UPDATE
  executed  = VALUES(executed),
  success   = VALUES(success),
  error     = VALUES(error)
;
