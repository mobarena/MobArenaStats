SELECT *
FROM
  (
    SELECT
      COUNT(1) AS total_sessions,
      SUM(TIMESTAMPDIFF(second, start_time, end_time)) AS total_seconds,
      SUM(last_wave) AS total_waves
    FROM sessions
  ) AS t1,
  (
    SELECT
      SUM(p.kills) AS total_kills
    FROM sessions s
    JOIN player_sessions p
      ON p.session_id = s.id
  ) AS t2;
