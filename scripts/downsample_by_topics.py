import random
from sklearn.externals import joblib
import argparse
from itertools import groupby
import numpy as np


def get_top_topic(l):
    doc_topics = l.split("\t")
    doc_id = doc_topics[1].split("/")[-1]
    top_topic = doc_topics[2]
    top_topic_score = float(doc_topics[3])
    return top_topic, doc_id, top_topic_score


def downsample_posting_ids(model_docs_path, outpath, sample_size):
    gby = lambda x: x[0]
    with open(model_docs_path, "r") as f:
        f.readline()
        top_topics = (get_top_topic(l) for l in f.readlines())
        docs_by_topic = {k:[p1[0] for p1 in sorted([p[1:] for p in v], key=lambda x: x[1], reverse=True)]
                for k,v in groupby(sorted(top_topics, key=gby), key=gby)}

    n_docs_by_topic = {k:len(v) for k,v in docs_by_topic.items()}
    n_docs = sum(n_docs_by_topic.values())
    topic_shares = {k:int(np.ceil((v/n_docs)*sample_size)) for k,v in n_docs_by_topic.items()}
    pids_out = []
    for k in topic_shares.keys():
        pids_out = pids_out + docs_by_topic[k][0:topic_shares[k]]

    joblib.dump(pids_out, outpath)


if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("model_docs_path")
    parser.add_argument("outpath")
    parser.add_argument("sample_size", type=int)
    args = parser.parse_args()
    downsample_posting_ids(args.model_docs_path, args.outpath, args.sample_size)


