import argparse
from itertools import chain


def parse_new_format(l):
    d = l.split()
    topics_ordered = list(chain(*
        [(str(i), "{:.10E}".format(x)) for i, x in 
        sorted(enumerate([float(x) for x in d[2:]]),
            key=lambda x: x[1], reverse=True)
        ]
        ))
    return d[0:2] + topics_ordered



def read_new_format(inpath):
    with open(inpath) as f:
        for l in f:
            yield parse_new_format(l)


def write_old_format(outpath, data):
    with open(outpath, "w") as f:
        f.write("#doc name topic proportion\n")
        for l in data:
            f.write("\t".join(l)+"\n")


if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("inpath")
    parser.add_argument("outpath")
    args = parser.parse_args()
    write_old_format(args.outpath, read_new_format(args.inpath))





