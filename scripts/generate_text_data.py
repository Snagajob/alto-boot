import argparse
import json
import os
import multiprocessing as mp
import logging
from sklearn.externals import joblib
from pymongo import MongoClient
import html2text
import re
import signal

def worker_init(outpath_in, mongo_host):
    global header_pattern
    global newline_pattern
    header_pattern = re.compile("(^|\n)#{1,}\s+")
    newline_pattern = re.compile("\n\s+\n")

    signal.signal(signal.SIGINT, signal.SIG_IGN)
    global outpath
    global client
    outpath = outpath_in
    client = MongoClient(host=mongo_host)

    global stops
    global noisy_paragraphs
    stops = load_stopwords()
    noisy_paragraphs = load_noisy_paragraphs()


def scrub(text):
    for paragraph in noisy_paragraphs:
        text = text.replace(paragraph, "")
    text = text.replace("part time", "part-time")
    text = text.replace("full time", "full-time")
    text = text.replace("parttime", "part-time")
    text = text.replace("fulltime", "full-time")
    text = text.replace("full-time/part-time", "full-time or part-time ")
    text = text.replace("part-time/full-time", "full-time or part-time ")
    text = re.sub(header_pattern, "\n\n", text)
    text = re.sub(newline_pattern, "\n\n", text)
    text = text.strip()
    return text


def load_stopwords(stopwords_path="./nlp_resources/stopwords.lex"):
    with open(stopwords_path, "r") as f:
        stopwords = {w.strip("\n") for w in f.readlines()}
    return stopwords


def load_noisy_paragraphs(noisy_paragraphs_path="./nlp_resources/noisy_paragraphs.lex"):
    with open(noisy_paragraphs_path, "r") as f:
        noisy_paragraphs = {p.strip("\n") for p in f.readlines()}
    return noisy_paragraphs


def json_to_corpus_text(posting_id):
    h2t = html2text.HTML2Text()
    h2t.ignore_links = True
    h2t.ignore_images = True
    h2t.ignore_emphasis = True
    h2t.ignore_anchors = True
    h2t.ul_item_mark = "-"
    h2t.body_width = 0

    posting = client.get_database("posting").get_collection("posting").find_one(
                {"_id":"{}".format(posting_id)},
                {"jd":1, "jt":1, "jr":1}
            )

    with open("{}/{}".format(outpath, posting["_id"]), "w") as f:
            f.write(posting["jt"]+"\n\n")
            f.write(scrub(h2t.handle(posting["jd"])))
            f.write("\n\n")
            f.write(scrub(h2t.handle(posting.get("jr",""))))



if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("sample_ids_path")
    parser.add_argument("outpath")
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

    uniq_local_postings = {str(p) for p in joblib.load(args.sample_ids_path)}
    with mp.Pool(mp.cpu_count(), worker_init, (args.outpath, mongo_host,)) as pool:
        try:
            pool.map(json_to_corpus_text, uniq_local_postings)
        except KeyboardInterrupt:
            pool.terminate()



