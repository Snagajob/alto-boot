# coding: utf-8

import argparse
from sklearn.externals import joblib
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.feature_extraction import DictVectorizer
from sklearn.datasets import dump_svmlight_file
import scipy.sparse as sp
import os

def load_doc(doc_id, corpus):
    with open("text_data/{}/{}".format(corpus, doc_id)) as f:
        return f.read()


def parse_topic_scores(l):
    d = l.split("\t")
    doc_id = d[1].split("/")[-1]
    scores = d[2:]
    scores = {scores[i]:float(scores[i+1]) for i in range(0,len(scores),2)}
    return doc_id, scores


def load_topic_scores(corpus, num_topics):
    topic_vec = DictVectorizer(sparse=True, dtype=float)
    with open("data/{}/output/T{}/init/model.docs".format(corpus, num_topics)) as f:
        f.readline()
        score = parse_topic_scores(f.readline())
        topic_vec.vocabulary_ = {k:i for i,k in enumerate(sorted(score[1].keys()))}
        topic_vec.feature_names_ = sorted(score[1].keys())
    with open("data/{}/output/T{}/init/model.docs".format(corpus, num_topics)) as f:
        f.readline()
        X_topics = topic_vec.transform(
                (d[1] for d in sorted((parse_topic_scores(l) for l in f.readlines()), key = lambda x: x[0]))
                )
    return topic_vec, X_topics


def worker_init(mongo_host):
    global client
    client = MongoClient(host=mongo_host)


def get_metadata_features(doc_id):
    posting = client.get_database("posting").get_collection("posting").find_one(
            {"_id":"{}".format(posting_id)},
            {"bid":1, "i":1, "cls":1}
            )
    return posting


if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("corpus")
    parser.add_argument("num_topics", type=int)
    parser.add_argument("mongo_user")
    parser.add_argument("mongo_pw")
    parser.add_argument("mongo_host")
    parser.add_argument("mongo_port")
    parser.add_argument("mongo_db")
    args = parser.parse_args()

    mongo_host = "mongodb://{user}:{password}@{host}:{port}/{database}".format(
            user=args.mongo_user,
            password=args.mongo_pw,
            host=args.mongo_host,
            port=args.mongo_port,
            database=args.mongo_db
    )
        
    corpus = "samp"
    num_topics = 10

    doc_ids = sorted(os.listdir("text_data/{}/".format(corpus)))
    token_vec = TfidfVectorizer(ngram_range=(1,2), min_df=int(0.01*len(doc_ids)))

    X_tokens = token_vec.fit_transform((load_doc(doc_id, corpus) for doc_id in doc_ids))
    topic_vec, X_topics = load_topic_scores(corpus, num_topics) 

    X_feats = sp.hstack([X_tokens, X_topics])
    print(X_feats.shape)

    dump_svmlight_file(X_feats.toarray(), [int(doc_id) for doc_id in doc_ids],
            "data/{}/output/T{}/init/{}.feat".format(corpus, num_topics, corpus),
            zero_based=False)

    joblib.dump(token_vec, "data/{}/output/T{}/init/{}_token.vec".format(corpus, num_topics, corpus))
    joblib.dump(topic_vec, "data/{}/output/T{}/init/{}_topic.vec".format(corpus, num_topics, corpus))

