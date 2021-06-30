-- Overall session data
CREATE TABLE IF NOT EXISTS sessions (
  id          INTEGER     PRIMARY KEY AUTOINCREMENT,
  session_id  TEXT        NOT NULL,
  arena_slug  TEXT        NOT NULL,
  start_time  TIMESTAMP   NOT NULL,
  end_time    TIMESTAMP   NOT NULL,
  last_wave   INTEGER     NOT NULL,
  conclusion  TEXT        NOT NULL
);

-- Create a unique index on the UUID for "player queries"
CREATE UNIQUE INDEX idx_sessions_session_id ON sessions (session_id);

-- Create an index on the arena slug for "arena queries"
CREATE INDEX idx_sessions_arena_slug ON sessions (arena_slug);
