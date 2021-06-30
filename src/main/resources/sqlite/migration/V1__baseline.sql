-- Schema migrations
CREATE TABLE IF NOT EXISTS schema_migrations (
  filename  TEXT        PRIMARY KEY,
  executed  TIMESTAMP   NOT NULL,
  success   INTEGER     NOT NULL,
  error     TEXT        NULL
);
