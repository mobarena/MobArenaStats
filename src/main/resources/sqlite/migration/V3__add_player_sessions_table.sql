-- Player-specific session data.
CREATE TABLE IF NOT EXISTS player_sessions (
  session_id    INTEGER   NOT NULL,
  player_id     TEXT      NOT NULL,
  player_name   TEXT      NOT NULL,
  class         TEXT      NOT NULL,
  join_time     TIMESTAMP NOT NULL,
  ready_time    TIMESTAMP NULL,
  leave_time    TIMESTAMP NULL,
  death_time    TIMESTAMP NULL,
  kills         INTEGER   NOT NULL,
  dmg_done      INTEGER   NOT NULL,
  dmg_taken     INTEGER   NOT NULL,
  swings        INTEGER   NOT NULL,
  hits          INTEGER   NOT NULL,
  last_wave     INTEGER   NOT NULL,
  conclusion    TEXT      NOT NULL,
  FOREIGN KEY (session_id) REFERENCES sessions (id) ON DELETE CASCADE
);

-- Create an index on the player UUID for "online queries"
CREATE INDEX idx_player_sessions_player_id ON player_sessions (player_id);

-- Create an index on the player name for "offline queries"
CREATE INDEX idx_player_sessions_player_name ON player_sessions (player_name);
