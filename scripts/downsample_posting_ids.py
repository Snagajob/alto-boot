import random
from sklearn.externals import joblib
import argparse

def downsample_posting_ids(inpath, outpath, sample_size):
    pids_in = joblib.load(inpath)
    pids_out = random.sample(pids_in, sample_size)
    joblib.dump(pids_out, outpath)

if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("inpath")
    parser.add_argument("outpath")
    parser.add_argument("sample_size", type=int)
    args = parser.parse_args()
    downsample_posting_ids(args.inpath, args.outpath, args.sample_size)


