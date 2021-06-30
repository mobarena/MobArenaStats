-- Player-specific session data.
CREATE TABLE IF NOT EXISTS player_sessions (
  session_id    INTEGER       NOT NULL,
  player_id     CHAR(36)      NOT NULL,
  player_name   VARCHAR(30)   NOT NULL,
  class         VARCHAR(30)   NOT NULL,
  join_time     DATETIME      NOT NULL,
  ready_time    DATETIME      NULL,
  leave_time    DATETIME      NULL,
  death_time    DATETIME      NULL,
  kills         INTEGER       NOT NULL,
  dmg_done      INTEGER       NOT NULL,
  dmg_taken     INTEGER       NOT NULL,
  swings        INTEGER       NOT NULL,
  hits          INTEGER       NOT NULL,
  last_wave     INTEGER       NOT NULL,
  conclusion    VARCHAR(10)   NOT NULL,
  FOREIGN KEY (session_id) REFERENCES sessions (id) ON DELETE CASCADE
);

-- Create an index on the player UUID for "online queries"
CREATE INDEX idx_player_sessions_player_id ON player_sessions (player_id);

-- Create an index on the player name for "offline queries"
CREATE INDEX idx_player_sessions_player_name ON player_sessions (player_name);
