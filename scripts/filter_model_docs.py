import argparse

def filter_model_docs(inpath, outpath):
    with open(inpath) as infile:
        with open(outpath, "w") as outfile:
            for l in infile:
                if l.strip().split("\t")[-1] != "0.01":
                    outfile.write(l)

if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("inpath")
    parser.add_argument("outpath")
    args = parser.parse_args()
    filter_model_docs(args.inpath, args.outpath)






