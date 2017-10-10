from xml.etree.ElementTree import fromstring
from xmljson import badgerfish as bf
import os

def get_metric(corpus, model, metric):
    with open("data/{}/output/{}/init/model_diagnostics.xml".format(corpus, model)) as f:
        d = bf.data(fromstring(f.read()))
    metrics = [d["model"]["topic"][i][metric] for i in range(int(model[1:]))]
    return metrics

def get_topic_terms(corpus, model, topic_num):
    with open("data/{}/output/{}/init/model_diagnostics.xml".format(corpus, model)) as f:
        d = bf.data(fromstring(f.read()))
    terms = [t["$"] for t in d["model"]["topic"][topic_num]["word"]]
    return terms

#models = os.listdir(outputdir)




