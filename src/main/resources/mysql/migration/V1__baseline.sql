-- Schema migrations
CREATE TABLE IF NOT EXISTS schema_migrations (
  filename  VARCHAR(60)   PRIMARY KEY,
  executed  TIMESTAMP     NOT NULL,
  success   BOOLEAN       NOT NULL,
  error     TEXT          NULL
);
