CORPUS=$1
BASEDIR=$2
MALLET_HOME=$3

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
    --max-idf 7.5 \
    --min-idf 0.1

