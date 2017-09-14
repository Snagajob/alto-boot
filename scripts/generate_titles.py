import argparse
import json
import os
import multiprocessing as mp
import logging
from sklearn.externals import joblib
import signal
from pymongo import MongoClient

def worker_init(mongo_host):
    signal.signal(signal.SIGINT, signal.SIG_IGN)
    global client
    client = MongoClient(host=mongo_host)

def json_to_posting_title(posting_id):
    posting = client.get_database("posting").get_collection("posting").find_one(
                {"_id":"{}".format(posting_id)},
                {"jt":1}
            )
    posting["jt"] = posting["jt"].replace("#", "").replace("\n"," ")
    return '{_id}#{jt}\n'.format(** posting)


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
    with mp.Pool(mp.cpu_count(), worker_init, (mongo_host,)) as pool:
        try:
            with open(args.outpath, "w") as f:
                for ptitle in pool.imap(json_to_posting_title, uniq_local_postings):
                    f.write(ptitle)
        except KeyboardInterrupt:
            pool.terminate()


