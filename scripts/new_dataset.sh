CORPUS=$1
BASEDIR=$2
SAMPLE_IDS_PATH=$3
NUMTOPICS=$4
MONGO_PW=$5

TEXTDATAPATH="$BASEDIR/text_data/$CORPUS"
rm -r $TEXTDATAPATH
mkdir $TEXTDATAPATH
rm -r ${BASEDIR}/WebContent/results/${CORPUS}
mkdir ${BASEDIR}/WebContent/results/${CORPUS}

INPUTPATH="${BASEDIR}/WebContent/results/${CORPUS}/input/"
mkdir $INPUTPATH
mkdir ${BASEDIR}/WebContent/results/${CORPUS}/log/
mkdir ${BASEDIR}/WebContent/results/${CORPUS}/output/
mkdir ${BASEDIR}/WebContent/results/${CORPUS}/output/T${NUMTOPICS}/
mkdir ${BASEDIR}/WebContent/results/${CORPUS}/output/T${NUMTOPICS}/init/

rm WebContent/data/${CORPUS}.html
rm WebContent/data/${CORPUS}.titles 

python scripts/generate_text_data.py ${SAMPLE_IDS_PATH} ${MONGO_PW} ${TEXTDATAPATH} &
python scripts/generate_html.py ${SAMPLE_IDS_PATH} ${MONGO_PW} WebContent/data/${CORPUS}.html & 
python scripts/generate_titles.py ${SAMPLE_IDS_PATH} ${MONGO_PW} WebContent/data/${CORPUS}.titles &
python scripts/generate_url.py $CORPUS ${SAMPLE_IDS_PATH} ${INPUTPATH}/${CORPUS}.url &

wait

$HOME/tree-TM/bin/mallet import-dir \
    --input $BASEDIR/text_data/$CORPUS \
    --output $BASEDIR/WebContent/results/$CORPUS/input/$CORPUS-topic-input.mallet \
    --remove-stopwords TRUE \
    --keep-sequence TRUE
    --gram-sizes 1,2 \
    --keep-sequence-bigrams TRUE

$HOME/tree-TM/bin/mallet train-topics \
    --input $BASEDIR/WebContent/results/$CORPUS/input/$CORPUS-topic-input.mallet \
    --num-topics $NUMTOPICS \
    --topic-word-weights-file \
    $BASEDIR/WebContent/results/$CORPUS/output/T${NUMTOPICS}/init/model.topics \
    --output-doc-topics \
    $BASEDIR/WebContent/results/$CORPUS/output/T${NUMTOPICS}/init/model.docs \
    --optimize-interval 10 \
    --num-threads 30 \
    --output-topic-keys topic-keys.txt \
    --output-doc-topics doc-topics.txt


