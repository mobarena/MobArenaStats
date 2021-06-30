-- Overall session data
CREATE TABLE IF NOT EXISTS sessions (
  id          INTEGER       PRIMARY KEY AUTO_INCREMENT,
  session_id  CHAR(36)      NOT NULL,
  arena_slug  VARCHAR(30)   NOT NULL,
  start_time  DATETIME      NOT NULL,
  end_time    DATETIME      NOT NULL,
  last_wave   INTEGER       NOT NULL,
  conclusion  VARCHAR(10)   NOT NULL
);

-- Create a unique index on the UUID for "player queries"
CREATE UNIQUE INDEX idx_sessions_session_id ON sessions (session_id);

-- Create an index on the arena slug for "arena queries"
CREATE INDEX idx_sessions_arena_slug ON sessions (arena_slug);
