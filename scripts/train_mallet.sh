CORPUS=$1
BASEDIR=$2
NUMTOPICS=$3

$HOME/tree-TM/bin/mallet import-dir \
    --input $BASEDIR/text_data/$CORPUS \
    --output $BASEDIR/WebContent/results/$CORPUS/input/$CORPUS-topic-input.mallet \
    --remove-stopwords TRUE \
    --keep-sequence TRUE \
    --gram-sizes 1,2 \
    --keep-sequence-bigrams


$HOME/tree-TM/bin/mallet train-topics \
    --input $BASEDIR/WebContent/results/$CORPUS/input/$CORPUS-topic-input.mallet \
    --output-doc-topics $BASEDIR/WebContent/results/$CORPUS/output/T${NUMTOPICS}/init/model.docs \
    --topic-word-weights-file $BASEDIR/WebContent/results/$CORPUS/output/T${NUMTOPICS}/init/model.topics \
    --num-topics $NUMTOPICS \
    --optimize-interval 10 \
    --num-threads 30 \
    --output-topic-keys topic-keys.txt 

