import argparse
import json
import os
import multiprocessing as mp
import logging
from sklearn.externals import joblib
import signal
from pymongo import MongoClient

head = """
<html>

<head>


<script src="http://ajax.googleapis.com/ajax/libs/jquery/1.5/jquery.min.js"></script>
<script type="text/javascript" charset="utf-8" src="../static/js/Label.js"></script>
<script type="text/javascript" charset="utf-8" src="../static/js/data.js"></script>
<link rel="stylesheet" href="../static/css/data.css">

</head>

<body onload="setTimeout(function(){addDocLabel();}, 100);">

<div class="transmenu" id = "save-labels"><p><input type="button" style="font-size:100%" value="Save Labels" onclick="saveDocLabelMap
()"/></p></div>
<form name="mainForm">
<div style="display:none" id="main" class="main">
"""

posting_templ = """
<div class="segment" id="{_id}">
<p>
{po}
</p>
</div>
"""

tail = """
</div>
</form>

</body>

</html>
"""

def worker_init(mongo_host):
    signal.signal(signal.SIGINT, signal.SIG_IGN)
    global client
    client = MongoClient(host=mongo_host)


def json_to_posting_html(posting_id):
    posting = client.get_database("posting").get_collection("posting").find_one(
                {"_id":"{}".format(posting_id)},
                {"po":1}
            )
    return posting_templ.format(** posting)


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
        with open(args.outpath, "w") as f:
            f.write(head)
            for phtml in pool.imap(json_to_posting_html, uniq_local_postings):
                f.write(phtml)
            f.write(tail)


