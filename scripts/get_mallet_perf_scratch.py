# coding: utf-8

from xml.etree.ElementTree import fromstring
from xmljson import badgerfish as bf
with open("data/large/output/T10/init/model_diagnostics.xml") as f:
    d = bf.data(fromstring(f.read()))
    
d
d.keys()
d["model"]
d["model"].keys()
d["model"]["topic"]
d["model"]["topic"].keys()
d["model"]["topic"][0]
d["model"]["topic"][0].keys()
d["model"]["topic"][0]["@coherence"]
[d["model"]["topic"][i]["@coherence"] for i in range(10)]
with open("data/large/output/T10/init/model_diagnostics.xml") as f:
    d10 = bf.data(fromstring(f.read()))
    
with open("data/large/output/T20/init/model_diagnostics.xml") as f:
    d20 = bf.data(fromstring(f.read()))
    
with open("data/large/output/T40//init/model_diagnostics.xml") as f:
    d40 = bf.data(fromstring(f.read()))
    
with open("data/large/output/T50//init/model_diagnostics.xml") as f:
    d50 = bf.data(fromstring(f.read()))
    
coh10 = [d10["model"]["topic"][i]["@coherence"] for i in range(10)]
coh20 = [d20["model"]["topic"][i]["@coherence"] for i in range(10)]
coh40 = [d40["model"]["topic"][i]["@coherence"] for i in range(10)]
coh50 = [d50["model"]["topic"][i]["@coherence"] for i in range(10)]
coh10
get_ipython().magic('history')
import os
os.listdir("data/large/output/")
def get_coherence(corpus, model):
    with open("data/{}/output/{}/init/model_diagnostics.xml".format(corpus, model)) as f:
        d = bf.data(fromstring(f.read()))
    coh = [d["model"]["topic"][i]["@coherence"] for i in range(int(model[1:]))]
    return coh
models = os.listdir("data/large/output/")
get_ipython().magic('ls data/large/output/T60/')
get_ipython().magic('ls data/large/output/T60/init/')
models
models = models[1:]
models
coh = {m:get_coherence("large", m) for m in models}
coh
import numpy
import numpy as np
coh = {m:np.mean(get_coherence("large", m)) for m in models}
coh
d
d["model"]["topic"]
d["model"]["topic"][0]
d["model"]["topic"][0].keys()
d["model"]["topic"][0]["@document_entropy"]
get_ipython().magic('cpaste')
get_ipython().magic('cpaste')
coh = {m:np.mean(get_metric("large", m, "@coherence")) for m in models}
coh
d["model"]["topic"][0].keys()
coh = {m:np.mean(get_metric("large", m, "@coherence")) for m in models}
coh = {m:np.exp(np.mean(get_metric("large", m, "@coherence"))) for m in models}
coh
coh = {m:np.mean(get_metric("large", m, "@coherence")) for m in models}
coh
np.log(0.10)
np.log(1)
np.log(0.0001)
d["model"]["topic"][0].keys()
unif = {m:np.mean(get_metric("large", m, "@uniform_dist")) for m in models}
unif
ent = {m:np.mean(get_metric("large", m, "@document_entropy")) for m in models}
ent
models = os.listdir("data/large/output/")
models
models = models[0:-1]
models
ent = {m:np.mean(get_metric("large", m, "@document_entropy")) for m in models}
ent
coh
coh = {m:np.mean(get_metric("large", m, "@coherence")) for m in models}
coh
unif = {m:np.mean(get_metric("large", m, "@uniform_dist")) for m in models}
unif
eff_num_word = {m:np.mean(get_metric("large", m, "@eff_num_wor")) for m in models}
eff_num_word = {m:np.mean(get_metric("large", m, "@eff_num_word")) for m in models}
d["model"]["topic"][0].keys()
eff_num_word = {m:np.mean(get_metric("large", m, "@eff_num_words")) for m in models}
eff_num_word
{m:np.mean(get_metric("large", m, "@corpus_dist")) for m in models}
coh
get_metric("large", "T60", "@rank_1_docs")
sorted(get_metric("large", "T60", "@rank_1_docs"))
sorted(enumerate(get_metric("large", "T60", "@rank_1_docs")), key=lambda x: x[1])
d["model"]["topic"][0]
d["model"]["topic"][0].keys()
d["model"]["topic"][0]["word"]
d["model"]["topic"][0]["word"][0]
d["model"]["topic"][0]["word"][0]["$"]
get_ipython().magic('cpaste')
sorted(enumerate(get_metric("large", "T60", "@rank_1_docs")), key=lambda x: x[1])
get_topic_terms("large", "T60", 20)
sorted(enumerate(get_metric("large", "T60", "@rank_1_docs")), key=lambda x: x[1])[0:3]
get_topic_terms("large", "T60", 55)
get_topic_terms("large", "T60", 44)
sorted(enumerate(get_metric("large", "T60", "@rank_1_docs")), key=lambda x: x[1])[0:5
]
get_topic_terms("large", "T60", 27)
get_topic_terms("large", "T60", 33)
sorted(enumerate(get_metric("large", "T60", "@allocation")), key=lambda x: x[1])[0:5]
d["model"]["topic"][0].keys()
sorted(enumerate(get_metric("large", "T60", "@allocation_ratio")), key=lambda x: x[1])[0:5]
sorted(enumerate(get_metric("large", "T60", "@rank_1_docs")), key=lambda x: x[1])[0:5]
with open("nlp_resources/stopwords.lexicon", "a") as f:
    for t in sorted(enumerate(get_metric("large", "T60", "@rank_1_docs")), key=lambda x: x[1])[0:3]:
        for term in get_topic_terms("large", "T60", t[0]):
            f.write(term+"\n")
            
sorted(enumerate(get_metric("large", "T60", "@exclusivity")), key=lambda x: x[1])[0:5]
sorted(enumerate(get_metric("large", "T60", "@exclusivity")), key=lambda x: x[1], reverse=True)[0:5]
get_topic_terms("large", "T60", 24)
get_topic_terms("large", "T60", 41)
get_topic_terms("large", "T60", 17)
get_topic_terms("large", "T60", 27)
sorted(enumerate(get_metric("large", "T60", "@exclusivity")), key=lambda x: x[1])[0:5]
get_topic_terms("large", "T60", 0)
get_topic_terms("large", "T60", 49)
get_topic_terms("large", "T60", 38)
sorted(enumerate(get_metric("large", "T60", "@coherence")), key=lambda x: x[1])[0:5]
get_topic_terms("large", "T60", 53)
get_topic_terms("large", "T60", 50)
get_topic_terms("large", "T60", 18)
sorted(enumerate(get_metric("large", "T60", "@coherence")), key=lambda x: x[1], reverse=True)[0:5]
get_topic_terms("large", "T60", 35)
get_topic_terms("large", "T60", 1)
get_topic_terms("large", "T60", 21)
get_topic_terms("large", "T60", 58)
models = os.listdir("data/large/output/")
models
models.remove("T100")
models
coh = {m:np.mean(get_metric("large", m, "@coherence")) for m in models}
models
coh
sorted(enumerate(get_metric("large", "T80", "@rank_1_docs")), key=lambda x: x[1])[0:5]
get_topic_terms("large", "T80", 17)
get_topic_terms("large", "T80", 44)
get_topic_terms("large", "T80", 18)
get_topic_terms("large", "T80", 15)
sorted(enumerate(get_metric("large", "T80", "@rank_1_docs")), key=lambda x: x[1])[0:5]
get_topic_terms("large", "T80", 56)
sorted(enumerate(get_metric("large", "T80", "@rank_1_docs")), key=lambda x: x[1], reverse=True)[0:5]
get_topic_terms("large", "T80", 32)
get_topic_terms("large", "T80", 53)
get_topic_terms("large", "T80", 1)
get_topic_terms("large", "T80", 69)
sorted(enumerate(get_metric("large", "T80", "@rank_1_docs")), key=lambda x: x[1], reverse=True)[0:5]
get_topic_terms("large", "T80", 61)
d["model"]["topic"][0].keys()
sorted(enumerate(get_metric("large", "T80", "@token-doc-diff")), key=lambda x: x[1], reverse=True)[0:5]
get_topic_terms("large", "T80", 53)
sorted(enumerate(get_metric("large", "T80", "@token-doc-diff")), key=lambda x: x[1])[0:5]
get_topic_terms("large", "T80", 66)
get_topic_terms("large", "T80", 55)
get_topic_terms("large", "T80", 6)
get_topic_terms("large", "T80", 18)
get_topic_terms("large", "T80", 17)
sorted(enumerate(get_metric("large", "T100", "@token-doc-diff")), key=lambda x: x[1])[0:5]
get_topic_terms("large", "T100", 23)
get_topic_terms("large", "T100", 66)
get_topic_terms("large", "T100", 68)
get_topic_terms("large", "T100", 19)
sorted(enumerate(get_metric("large", "T100", "@token-doc-diff")), key=lambda x: x[1])[0:10]
sorted(enumerate(get_metric("large", "T100", "@token-doc-diff")), key=lambda x: x[1])[0:20]
models = os.listdir("data/large/output/")
models
models.remove("T150")
coh = {m:np.mean(get_metric("large", m, "@coherence")) for m in models}
coh
get_topic_terms("large", "T100", 20)
sorted(enumerate(get_metric("large", "T100", "@token-doc-diff")), key=lambda x: x[1], reverse=True)[0:20]
sorted(enumerate(get_metric("large", "T100", "@rank_1_docs")), key=lambda x: x[1])[0:5]
sorted(enumerate(get_metric("large", "T100", "@rank_1_docs")), key=lambda x: x[1])[0:10]
sorted(enumerate(get_metric("large", "T100", "@rank_1_docs")), key=lambda x: x[1], reverse=True)[0:10]
get_topic_terms("large", "T100", 32)
get_topic_terms("large", "T100", 17)
get_topic_terms("large", "T100", 19)
sorted(enumerate(get_metric("large", "T100", "@rank_1_docs")), key=lambda x: x[1])[0:10]
get_topic_terms("large", "T100", 95)
get_topic_terms("large", "T100", 68)
get_topic_terms("large", "T100", 46)
get_topic_terms("large", "T100", 26)
with open("nlp_resources/stopwords.lexicon", "a") as f:
    for t in sorted(enumerate(get_metric("large", "T100", "@rank_1_docs")), key=lambda x: x[1])[0:4]:
        for term in get_topic_terms("large", "T100", t[0]):
            f.write(term+"\n")
            
