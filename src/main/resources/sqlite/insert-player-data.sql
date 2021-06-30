INSERT INTO player_sessions (
  player_id,
  player_name,
  session_id,
  class,
  join_time,
  ready_time,
  leave_time,
  death_time,
  kills,
  dmg_done,
  dmg_taken,
  swings,
  hits,
  last_wave,
  conclusion
) VALUES (
  :player_id,
  :player_name,
  (
    SELECT id
    FROM sessions s
    WHERE s.session_id = :session_id
  ),
  :class,
  :join_time,
  :ready_time,
  :leave_time,
  :death_time,
  :kills,
  :dmg_done,
  :dmg_taken,
  :swings,
  :hits,
  :last_wave,
  :conclusion
);
