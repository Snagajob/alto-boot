CREATE DATABASE alto;

CREATE TABLE corpora
(
    corpus_id serial PRIMARY KEY,
    corpus_name varchar(50) NOT NULL,
    number_of_docs integer NOT NULL,
    number_of_topics integer NOT NULL,
    number_of_features integer NOT NULL
);

CREATE TABLE documents
(
    document_id serial PRIMARY KEY,
    external_document_id varchar(100) UNIQUE NULL, -- link to external system if desired
    document_title varchar(200) NOT NULL,
    document_content text NOT NULL,
    document_display text NULL, -- if desired, display can be different from content
    document_weight numeric NOT NULL
);

CREATE TABLE corpora_documents -- document can be in multiple corpora
(
    corpus_id integer NOT NULL REFERENCES corpora ON DELETE CASCADE,
    document_id integer NOT NULL REFERENCES documents ON DELETE CASCADE,
    PRIMARY KEY (corpus_id, document_id) 
);

CREATE TABLE users
(
    user_id serial PRIMARY KEY,
    name varchar(100),
    created date,
    last_login date
);

CREATE TABLE sessions 
(
    session_id serial PRIMARY KEY,
    user_id integer REFERENCES users,
    corpus_id integer REFERENCES corpora,
    created date,
    last_accessed date
);

-- Topic Model-Related Tables
CREATE TABLE topics -- corpus topics
(
    topic_id serial PRIMARY KEY,
    topic_ordinal integer NOT NULL,
    corpus_id integer NOT NULL REFERENCES corpora ON DELETE CASCADE,
    UNIQUE(topic_ordinal, corpus_id)
);

CREATE TABLE topic_terms --- topics -> terms, weights, 
-- currently data/{CORPUS_NAME}/output/T{NTOPICS}/init/model.topics
(
    topic_id integer NOT NULL REFERENCES topics,
    term varchar(200) NOT NULL,
    weight numeric NOT NULL -- term importance to topic
    UNIQUE(topic_id, term)
);

CREATE TABLE document_topics -- document topic probabilities 
-- currently data/{CORPUS_NAME}/output/T{NTOPICS}/init/model.topics
(
    document_id integer NOT NULL references documents,
    topic_id integer NOT NULL REFERENCES topics,
    topic_probablity numeric NOT NULL,
    PRIMARY KEY (document_id, topic_id)
);

-- Active Learning Related Tables
CREATE TYPE label_creation_source as ENUM ( "default", "created", "renamed" );
CREATE TABLE labels
(
    label_id serial PRIMARY KEY,
    parent_label_id integer NULL REFERENCES labels ON DELETE SET NULL,
    corpus_id integer REFERENCES corpora,
    user_id integer NULL REFERENCES users,
    name varchar(200) NOT NULL,
    source label_type NOT NULL
);

CREATE TABLE session_labels -- labels associated with particular session
(
    session_id integer NOT NULL REFERENCES corpora ON DELETE CASCADE,
    label_id integer NOT NULL REFERENCES labels ON DELETE CASCADE,
    PRIMARY KEY (session_id, label_id) 
);

CREATE TABLE document_features -- features for active learning model
(
    corpus_id integer NOT NULL REFERENCES corpora,
    document_id integer NOT NULL references documents,
    feature_ordinal integer NOT NULL,
    feature_name varchar(100) NOT NULL,
    feature_value numeric NOT NULL,
    PRIMARY KEY (corpus_id, document_id, feature_ordinal)
);

CREATE TYPE document_label_source as ENUM ( "predicted", "assigned" );
CREATE TABLE document_labels -- document labels
(
    session_id integer NOT NULL references sessions,
    label_id integer NOT NULL references labels
    document_id integer NOT NULL references documents,
    score numeric NULL, -- null if assigned, score between 0.0 and 1.0 if predicted
    source document_label_source, 
    PRIMARY KEY (session_id, document_id, label_id)
);

