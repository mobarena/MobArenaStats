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
) ON CONFLICT (filename) DO UPDATE SET
  executed  = excluded.executed,
  success   = excluded.success,
  error     = excluded.error
;
