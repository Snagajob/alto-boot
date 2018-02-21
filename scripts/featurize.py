# coding: utf-8

import argparse
from sklearn.externals import joblib
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.feature_extraction import DictVectorizer
from sklearn.datasets import dump_svmlight_file
from pymongo import MongoClient
import scipy.sparse as sp
import os
import multiprocessing as mp

def load_doc(posting_id, corpus):
    with open("text_data/{}/{}".format(corpus, posting_id)) as f:
        return f.read()


def parse_topic_scores(l):
    d = l.split("\t")
    posting_id = d[1].split("/")[-1]
    scores = d[2:]
    scores = {scores[i]:float(scores[i+1]) for i in range(0,len(scores),2)}
    return posting_id, scores


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


def get_metadata_features(posting_id):
    posting = client.get_database("posting").get_collection("posting").find_one(
            {"_id":"{}".format(posting_id)},
            {"bid":1, "baid":1, "i":1, "cls":1}
            )
    posting_metadata_features = dict()
    posting_metadata_features["brand_id"] = posting.get("bid")
    for cls in posting.get("cls", []):
        posting_metadata_features[cls["cn"]] = cls["s"]
    for i in posting.get("i", []):
        if i["n"] != "Other":
            if i["p"] == True:
                posting_metadata_features[i["n"]] = 1.0
            elif i["p"] == False:
                posting_metadata_features[i["n"]] = 0.5
    return posting_metadata_features


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
        

    posting_ids = sorted(os.listdir("text_data/{}/".format(args.corpus)))

    metadata_vec = DictVectorizer(sparse=True, dtype=float)
    with mp.Pool(mp.cpu_count(), worker_init, (mongo_host,)) as p:
        X_metadata = metadata_vec.fit_transform(p.imap(get_metadata_features, posting_ids))

    token_vec = TfidfVectorizer(ngram_range=(1,2), min_df=20, max_df=0.9, stop_words="english")
    X_tokens = token_vec.fit_transform((load_doc(posting_id, args.corpus) for posting_id in posting_ids))

    topic_vec, X_topics = load_topic_scores(args.corpus, args.num_topics) 

    X_feats = sp.hstack([X_tokens, X_metadata, X_topics])
    print(X_feats.shape)

    dump_svmlight_file(X_feats, [int(posting_id) for posting_id in posting_ids],
            "data/{}/output/T{}/init/{}.feat".format(args.corpus, args.num_topics, args.corpus),
            zero_based=False)

    joblib.dump(token_vec, "data/{}/output/T{}/init/{}_token.vec".format(args.corpus, args.num_topics, args.corpus))
    joblib.dump(metadata_vec, "data/{}/output/T{}/init/{}_metadata.vec".format(args.corpus, args.num_topics, args.corpus))
    joblib.dump(topic_vec, "data/{}/output/T{}/init/{}_topic.vec".format(args.corpus, args.num_topics, args.corpus))

