# coding: utf-8

from sklearn.feature_extraction.text import TfidfVectorizer
def load_doc(doc_id):
    with open("text_data/large/{}".format(doc_id)) as f:
        return f.read()
    
load_doc(10000086)
import os
doc_ids = os.listdir("text_data/large/")
len(doc_ids)
vec = TfidfVectorizer()
vec
vec = TfidfVectorizer(ngram_range=(1,2), max_df=0.8, min_df=1000)
X_large = vec.fit_transform((load_doc(doc_id) for doc_id in doc_ids))
X_large
X_large[0,:]
X_large[0,:].sum()
X_large[0:10,:].sum()
X_large[0:10,:].sum(1)
doc_feature_sums = X_large.sum(1)
doc_feature_sums[0]
doc_feature_sums
doc_feature_sums.tolist()
doc_feature_sums = doc_feature_sums.tolist()
doc_feature_sums[0]
doc_feature_sums[1]
from itertools import chain
doc_feature_sums = list(chain(*doc_feature_sums))
doc_feature_sums[0]
doc_feature_sums[-1]
docs_sorted = sorted(zip(doc_ids, doc_feature_sums), key=lambda x: x[1])
docs_sorted[0:10]
docs_sorted[0:100]
load_doc("42366040")
load_doc("41765534")
docs_sorted[0:1000]
load_doc('15249527")
load_doc("15249527")
load_doc("15249527"))
load_doc("15249527")
load_doc("35744560")
docs_sorted[0:1000]
docs_sorted[0:5000]
docs_sorted[0:5000][-1]
load_doc("35477422")
docs_sorted[0:10000][-1]
load_doc(docs_sorted[0:10000][-1][0])
load_doc(docs_sorted[0:20000][-1][0])
load_doc(docs_sorted[0:4000][-1][0])
load_doc(docs_sorted[0:2000][-1][0])
load_doc(docs_sorted[0:3000][-1][0])
load_doc(docs_sorted[0:2000][-1][0])
load_doc(docs_sorted[0:5000][-1][0])
from sklearn.externals import joblib
to_remove = {d[0] for d in docs_sorted[0:5000]}
training_set = joblib.load("/home/ubuntu/unique_postings_20170101_20170801.pkl")
training_set = [d for d in training_set if d not in to_remove]
len(training_set)
training_set = joblib.load("/home/ubuntu/unique_postings_20170101_20170801.pkl")
len(training_set)
to_remove = {d[0] for d in docs_sorted[0:10000]}
training_set = [d for d in training_set if d not in to_remove]
joblib.dump(training_set, "/home/ubuntu/unique_postings_20170101_20170801_pruned.pkl")
get_ipython().magic('save scripts/prune_documents.py 0-60')
