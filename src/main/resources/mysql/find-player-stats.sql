SELECT
  COUNT(1) AS total_sessions,
  SUM(TIMESTAMPDIFF(second, s.start_time, COALESCE(p.death_time, p.leave_time, s.end_time))) AS total_seconds,
  SUM(p.kills) AS total_kills,
  SUM(p.last_wave) AS total_waves
FROM sessions s
JOIN player_sessions p
  ON p.session_id = s.id
WHERE p.player_name = :player_name;
