SELECT *
FROM schema_migrations
WHERE success = TRUE
ORDER BY filename;
