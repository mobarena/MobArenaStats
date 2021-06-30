SELECT *
FROM
  (
    SELECT
      COUNT(1) AS total_sessions,
      MAX(last_wave) AS highest_wave,
      SUM(last_wave) AS total_waves,
      MAX(TIMESTAMPDIFF(second, start_time, end_time)) AS highest_seconds,
      SUM(TIMESTAMPDIFF(second, start_time, end_time)) AS total_seconds
    FROM sessions
    WHERE arena_slug = :arena_slug
  ) AS t1,
  (
    SELECT
      SUM(p.kills) AS total_kills,
      MAX(p.kills) AS highest_kills
    FROM sessions s
    JOIN player_sessions p
      ON p.session_id = s.id
    WHERE s.arena_slug = :arena_slug
  ) AS t2;
