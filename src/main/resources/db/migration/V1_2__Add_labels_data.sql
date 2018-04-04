
INSERT INTO users (user_name) VALUES ('system') ;
INSERT INTO corpora (corpus_name) VALUES ('synthetic') ;

-- insert default session..
INSERT INTO tagging_sessions(session_id, start_date, end_date, user_id) VALUES
    ('00000000-0000-0000-0000-000000000000','2018-01-01','2018-01-01',(select user_id from users where user_name='system'));

-- default labels
INSERT INTO labels (corpus_id, label_name, label_source, session_id) VALUES
    ((SELECT corpus_id FROM corpora WHERE corpus_name='synthetic'), 'directions', 'DEFAULT', '00000000-0000-0000-0000-000000000000'),
    ((SELECT corpus_id FROM corpora WHERE corpus_name='synthetic'), 'animals', 'DEFAULT', '00000000-0000-0000-0000-000000000000'),
    ((SELECT corpus_id FROM corpora WHERE corpus_name='synthetic'), 'cleaning', 'DEFAULT', '00000000-0000-0000-0000-000000000000'),
    ((SELECT corpus_id FROM corpora WHERE corpus_name='synthetic'), 'numbers', 'DEFAULT', '00000000-0000-0000-0000-000000000000'),
    ((SELECT corpus_id FROM corpora WHERE corpus_name='synthetic'), 'days', 'DEFAULT', '00000000-0000-0000-0000-000000000000');
