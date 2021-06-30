SELECT *
FROM
  (
    SELECT
      COUNT(1) AS total_sessions,
      SUM((end_time / 1000) - (start_time / 1000)) AS total_seconds,
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
