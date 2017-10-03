CORPUS=$1
BASEDIR=$2
NUMTOPICS=$3
MALLET_HOME=$4
NUM_THREADS=$5

$MALLET_HOME/mallet import-dir \
    --input $BASEDIR/text_data/$CORPUS \
    --output $BASEDIR/data/$CORPUS/input/$CORPUS-topic-input-all.mallet \
    --remove-stopwords TRUE \
    --keep-sequence TRUE \
    --skip-html TRUE \
    --gram-sizes 1,2 \
    --keep-sequence-bigrams \
    --extra-stopwords $BASEDIR/nlp_resources/stopwords.lexicon

$MALLET_HOME/mallet prune \
    --input $BASEDIR/data/$CORPUS/input/$CORPUS-topic-input-all.mallet \
    --output $BASEDIR/data/$CORPUS/input/$CORPUS-topic-input.mallet \
    --max-idf 6.5 \
    --min-idf 0.2

mkdir -p $BASEDIR/data/$CORPUS/output/T${NUMTOPICS}/init/ 

$MALLET_HOME/mallet train-topics \
    --input $BASEDIR/data/$CORPUS/input/$CORPUS-topic-input.mallet \
    --output-doc-topics $BASEDIR/data/$CORPUS/output/T${NUMTOPICS}/init/model.docs.new \
    --topic-word-weights-file $BASEDIR/data/$CORPUS/output/T${NUMTOPICS}/init/model.topics \
    --num-topics $NUMTOPICS \
    --num-threads $NUM_THREADS \
    --optimize-interval 10 \
    --optimize-burn-in 200 \
    --num-icm-iterations 50 \
    --num-iterations 1250 \
    --output-model $BASEDIR/data/$CORPUS/output/T${NUMTOPICS}/init/trained-model.mallet 

python $BASEDIR/scripts/convert_model_docs.py \
    $BASEDIR/data/$CORPUS/output/T${NUMTOPICS}/init/model.docs.new \
    $BASEDIR/data/$CORPUS/output/T${NUMTOPICS}/init/model.docs

