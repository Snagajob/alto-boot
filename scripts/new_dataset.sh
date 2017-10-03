CORPUS=$1
BASEDIR=$2
SAMPLE_IDS_PATH=$3
NUMTOPICS=$4
MALLET_HOME=$5
NUM_THREADS_MALLET=$6
MONGO_USER=$7
MONGO_PW=$8
MONGO_HOST=$9
MONGO_PORT=${10}
MONGO_DB=${11}

TEXTDATAPATH="$BASEDIR/text_data/$CORPUS"
INPUTPATH="${BASEDIR}/data/${CORPUS}/input/"
OUTPUTPATH="${BASEDIR}/data/${CORPUS}/output/T${NUMTOPICS}/init/"

rm -r $TEXTDATAPATH
rm -r ${BASEDIR}/data/${CORPUS}

mkdir -p $TEXTDATAPATH
mkdir -p $INPUTPATH
mkdir -p $OUTPUTPATH

python scripts/generate_html.py ${SAMPLE_IDS_PATH} ${BASEDIR}/data/${CORPUS}.html \
    ${MONGO_USER} ${MONGO_PW} ${MONGO_HOST} ${MONGO_PORT} ${MONGO_DB} &
python scripts/generate_titles.py ${SAMPLE_IDS_PATH} ${BASEDIR}/data/${CORPUS}.titles \
    ${MONGO_USER} ${MONGO_PW} ${MONGO_HOST} ${MONGO_PORT} ${MONGO_DB} &
python scripts/generate_url.py $CORPUS ${SAMPLE_IDS_PATH} ${INPUTPATH}/${CORPUS}.url &
python scripts/generate_text_data.py ${SAMPLE_IDS_PATH} ${TEXTDATAPATH} \
    ${MONGO_USER} ${MONGO_PW} ${MONGO_HOST} ${MONGO_PORT} ${MONGO_DB} 

wait

bash $BASEDIR/scripts/train_mallet.sh $CORPUS $BASEDIR $NUMTOPICS $MALLET_HOME $NUM_THREADS_MALLET
