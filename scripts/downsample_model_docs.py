from sklearn.externals import joblib
import argparse


def downsample(pids_path, inpath, outpath):
    pids = set(joblib.load(pids_path))
    with open(inpath, "r") as infile:
        with open(outpath, "w") as outfile:
            outfile.write(infile.readline())
            i = 0
            for l in infile:
                d = l.split("\t")
                pid = d[1].split("/")[-1]
                if pid in pids:
                    d[0] = str(i)
                    outfile.write("\t".join(d))
                    i = i+1


if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("posting_ids_path")
    parser.add_argument("inpath")
    parser.add_argument("outpath")
    args = parser.parse_args()
    downsample(args.posting_ids_path, args.inpath, args.outpath)





