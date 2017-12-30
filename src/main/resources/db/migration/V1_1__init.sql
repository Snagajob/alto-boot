
CREATE TABLE document_labels -- document labels
(
    session_id integer NOT NULL,
    label_id integer NOT NULL,
    document_id integer NOT NULL,
    score numeric NULL, -- null if assigned, score between 0.0 and 1.0 if predicted
    PRIMARY KEY (session_id, document_id, label_id)
);