SELECT p.*
FROM player_sessions p
JOIN sessions s
  ON s.id = p.session_id
WHERE s.session_id = :session_id;
