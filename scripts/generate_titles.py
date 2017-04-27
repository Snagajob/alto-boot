import argparse
import json
import os
import multiprocessing as mp
import logging
from sklearn.externals import joblib


def json_to_posting_title(posting_json_path):
    with open(posting_json_path, "r") as f:
        posting = json.load(f)
        posting["data"]["jt"] = posting["data"]["jt"].replace("#", "").replace("\n"," ")
        return '{DIMJOBPOSTINGKEY}#{data[jt]}\n'.format(** posting)


def crawl_dir(dir_to_crawl, doc_set=set(), n=-1):
    i = 0;
    for dirpath, _, filenames in os.walk(dir_to_crawl):
        for f in filenames:
            if any([len(doc_set)==0, f.replace(".json","") in doc_set]):
                i = i + 1
                if i%10000==0:
                    logging.info("loaded {} postings".format(i))
                if all([n>0, i==n]):
                    raise StopIteration
                yield os.path.abspath(os.path.join(dirpath, f))


if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("inpath")
    parser.add_argument("outpath")
    parser.add_argument("--subset", type=int, default=-1)
    args = parser.parse_args()
    uniq_local_postings = {str(p) for p in joblib.load("uniq_local_postings.pkl")}
    with mp.Pool(mp.cpu_count()) as pool:
        with open(args.outpath, "w") as f:
            for ptitle in pool.imap(json_to_posting_title, crawl_dir(args.inpath, doc_set=uniq_local_postings, n=args.subset)):
                f.write(ptitle)


