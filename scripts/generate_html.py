import argparse
import json
import os
import multiprocessing as mp
import logging
from sklearn.externals import joblib

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
<div class="segment" id="{DIMJOBPOSTINGKEY}">
<p>
{data[topo]}
</p>
</div>
"""

tail = """
</div>
</form>

</body>

</html>
"""



def json_to_posting_html(posting_json_path):
    with open(posting_json_path, "r") as f:
        posting = json.load(f)
        return posting_templ.format(** posting)


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
            f.write(head)
            for phtml in pool.imap(json_to_posting_html, crawl_dir(args.inpath, doc_set=uniq_local_postings, n=args.subset)):
                f.write(phtml)
            f.write(tail)


