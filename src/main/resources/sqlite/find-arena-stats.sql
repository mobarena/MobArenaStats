SELECT *
FROM
  (
    SELECT
      COUNT(1) AS total_sessions,
      MAX(last_wave) AS highest_wave,
      SUM(last_wave) AS total_waves,
      MAX((end_time / 1000) - (start_time / 1000)) AS highest_seconds,
      SUM((end_time / 1000) - (start_time / 1000)) AS total_seconds
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
