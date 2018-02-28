CREATE TABLE corpora
(
    corpus_id SERIAL NOT NULL PRIMARY KEY,
    corpus_name VARCHAR(50) NOT NULL
);

CREATE TYPE label_creation_source as ENUM ( 'DEFAULT', 'CREATED', 'RENAMED' );
CREATE TABLE labels
(
    label_id SERIAL PRIMARY KEY,
    corpus_id integer REFERENCES corpora,
    label_name varchar(200) NOT NULL,
    label_source label_creation_source NOT NULL
);

INSERT INTO corpora (corpus_name) VALUES
    ('synthetic'),
    ('junk')
    ;

-- default labels
INSERT INTO labels (corpus_id, label_name, label_source) VALUES
    ((SELECT corpus_id FROM corpora WHERE corpus_name='synthetic'), 'directions', 'DEFAULT'),
    ((SELECT corpus_id FROM corpora WHERE corpus_name='synthetic'), 'animals', 'DEFAULT'),
    ((SELECT corpus_id FROM corpora WHERE corpus_name='synthetic'), 'cleaning', 'DEFAULT'),
    ((SELECT corpus_id FROM corpora WHERE corpus_name='synthetic'), 'numbers', 'DEFAULT'),
    ((SELECT corpus_id FROM corpora WHERE corpus_name='synthetic'), 'days', 'DEFAULT'),
    ((SELECT corpus_id FROM corpora WHERE corpus_name='junk'), 'junklabel1', 'DEFAULT'),
    ((SELECT corpus_id FROM corpora WHERE corpus_name='junk'), 'junklabel2', 'DEFAULT')
    ;

