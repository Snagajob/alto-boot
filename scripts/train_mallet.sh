CORPUS=$1
BASEDIR=$2
NUMTOPICS=$3
MALLET_HOME=$4
NUM_THREADS=$5

$MALLET_HOME/mallet import-dir \
    --input $BASEDIR/text_data/$CORPUS \
    --output $BASEDIR/data/$CORPUS/input/$CORPUS-topic-input.mallet \
    --remove-stopwords TRUE \
    --keep-sequence TRUE \
    --skip-html TRUE \
    --gram-sizes 1,2 \
    --keep-sequence-bigrams \
    --extra-stopwords $BASEDIR/nlp_resources/stopwords.lex

NUM_DOCS=$(ls ${BASEDIR}/text_data/${CORPUS} | wc -l)
PRUNE_COUNT=$(($NUM_DOCS/1000))

$MALLET_HOME/mallet prune \
    --input $BASEDIR/data/$CORPUS/input/$CORPUS-topic-input.mallet \
    --output $BASEDIR/data/$CORPUS/input/$CORPUS-topic-input-pruned.mallet \
    --prune-count $PRUNE_COUNT

mkdir -p $BASEDIR/data/$CORPUS/output/T${NUMTOPICS}/init/ 

$MALLET_HOME/mallet train-topics \
    --input $BASEDIR/data/$CORPUS/input/$CORPUS-topic-input.mallet \
    --output-doc-topics $BASEDIR/data/$CORPUS/output/T${NUMTOPICS}/init/model.docs \
    --topic-word-weights-file $BASEDIR/data/$CORPUS/output/T${NUMTOPICS}/init/model.topics \
    --num-topics $NUMTOPICS \
    --optimize-interval 10 \
    --num-threads $NUM_THREADS

