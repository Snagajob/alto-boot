CREATE TABLE corpora
(
    corpus_id SERIAL NOT NULL PRIMARY KEY,
    corpus_name VARCHAR(50) NOT NULL
);

CREATE TABLE users
(
    user_id SERIAL NOT NULL PRIMARY KEY,
    user_name VARCHAR(50) NOT NULL UNIQUE
);

-- CREATE TYPE label_creation_source AS ENUM ( 'DEFAULT', 'CREATED', 'RENAMED' );
CREATE TABLE labels
(
    label_id SERIAL PRIMARY KEY,
    corpus_id INTEGER REFERENCES corpora,
    label_name VARCHAR(200) NOT NULL,
    label_source VARCHAR NOT NULL,
    user_id INTEGER REFERENCES users NOT NULL -- created by
);

ALTER TABLE labels
    ADD CONSTRAINT check_label_source
    CHECK (label_source in ('DEFAULT', 'CREATED', 'RENAMED'));
INSERT INTO users (user_name) VALUES ('system') ;
INSERT INTO corpora (corpus_name) VALUES ('synthetic') ;

-- default labels
INSERT INTO labels (corpus_id, label_name, label_source, user_id) VALUES
    ((SELECT corpus_id FROM corpora WHERE corpus_name='synthetic'), 'directions', 'DEFAULT', (select user_id from users where user_name='system')),
    ((SELECT corpus_id FROM corpora WHERE corpus_name='synthetic'), 'animals', 'DEFAULT', (select user_id from users where user_name='system')),
    ((SELECT corpus_id FROM corpora WHERE corpus_name='synthetic'), 'cleaning', 'DEFAULT', (select user_id from users where user_name='system')),
    ((SELECT corpus_id FROM corpora WHERE corpus_name='synthetic'), 'numbers', 'DEFAULT', (select user_id from users where user_name='system')),
    ((SELECT corpus_id FROM corpora WHERE corpus_name='synthetic'), 'days', 'DEFAULT', (select user_id from users where user_name='system'))
    ;

