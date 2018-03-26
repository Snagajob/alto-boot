
CREATE TABLE tagging_sessions
(
    session_id uuid NOT NULL PRIMARY KEY,
    start_date timestamptz,
    end_date timestamptz,
    user_id INTEGER REFERENCES users NOT NULL
);


ALTER TABLE labels
    ADD COLUMN session_id uuid;
