INSERT INTO sessions (
  session_id,
  arena_slug,
  start_time,
  end_time,
  last_wave,
  conclusion
) VALUES (
  :session_id,
  :arena_slug,
  :start_time,
  :end_time,
  :last_wave,
  :conclusion
);
