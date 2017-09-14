import argparse
import json
import os
import multiprocessing as mp
import logging
from sklearn.externals import joblib


def worker_init(corpus_name):
    global corpus
    corpus = corpus_name


def json_to_posting_title(posting_id):
    url_templ = "{posting_id} /data/{corpus}.html#{posting_id}\n"
    return url_templ.format(posting_id=posting_id, corpus=corpus)


if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("corpus")
    parser.add_argument("sample_ids_path")
    parser.add_argument("outpath")
    args = parser.parse_args()
    uniq_local_postings = {str(p) for p in joblib.load(args.sample_ids_path)}
    with mp.Pool(mp.cpu_count(), worker_init, (args.corpus,) ) as pool:
        with open(args.outpath, "w") as f:
            for ptitle in pool.imap(json_to_posting_title, uniq_local_postings):
                f.write(ptitle)


