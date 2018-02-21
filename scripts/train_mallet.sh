CORPUS=$1
BASEDIR=$2
NUMTOPICS=$3
MALLET_HOME=$4
NUM_THREADS=$5

mkdir -p $BASEDIR/data/$CORPUS/output/T${NUMTOPICS}/init/ 

$MALLET_HOME/mallet train-topics \
    --input $BASEDIR/data/$CORPUS/input/$CORPUS-topic-input.mallet \
    --output-doc-topics $BASEDIR/data/$CORPUS/output/T${NUMTOPICS}/init/model.docs.new \
    --topic-word-weights-file $BASEDIR/data/$CORPUS/output/T${NUMTOPICS}/init/model.topics \
    --num-topics $NUMTOPICS \
    --num-threads $NUM_THREADS \
    --optimize-interval 10 \
    --optimize-burn-in 200 \
    --num-iterations 1250 \
    --diagnostics-file $BASEDIR/data/$CORPUS/output/T${NUMTOPICS}/init/model_diagnostics.xml \
    --output-model $BASEDIR/data/$CORPUS/output/T${NUMTOPICS}/init/trained-model.mallet 
#    --num-icm-iterations 50 \

python $BASEDIR/scripts/convert_model_docs.py \
    $BASEDIR/data/$CORPUS/output/T${NUMTOPICS}/init/model.docs.new \
    $BASEDIR/data/$CORPUS/output/T${NUMTOPICS}/init/model.docs

