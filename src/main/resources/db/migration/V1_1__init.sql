CREATE TABLE corpora
(
    corpus_id SERIAL NOT NULL PRIMARY KEY,
    corpus_name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE users
(
    user_id SERIAL NOT NULL PRIMARY KEY,
    user_name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE tagging_sessions
(
    session_id uuid NOT NULL PRIMARY KEY,
    start_date timestamptz,
    end_date timestamptz,
    user_id INTEGER REFERENCES users NOT NULL
);

-- CREATE TYPE label_creation_source AS ENUM ( 'DEFAULT', 'CREATED', 'RENAMED' );
CREATE TABLE labels
(
    label_id SERIAL PRIMARY KEY,
    corpus_id INTEGER REFERENCES corpora,
    label_name VARCHAR(200) NOT NULL,
    label_source VARCHAR NOT NULL,
    session_id uuid REFERENCES tagging_sessions NOT NULL
);

ALTER TABLE labels
    ADD CONSTRAINT check_label_source
CHECK (label_source in ('DEFAULT', 'CREATED', 'RENAMED'));


CREATE TABLE document_labels -- document labels
(
    session_id uuid NOT NULL,
    label_id integer NOT NULL,
    document_id VARCHAR(36) NOT NULL,
    score numeric NULL, -- null if assigned, score between 0.0 and 1.0 if predicted
    confirmed BOOLEAN,
    PRIMARY KEY (session_id, document_id, label_id)
);

