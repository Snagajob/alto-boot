CORPUS=$1
BASEDIR=$2
SAMPLE_IDS_PATH=$3
NUMTOPICS=$4
DOWNSAMPLE_SIZE=$5
MALLET_HOME=$6
MONGO_USER=$7
MONGO_PW=$8
MONGO_HOST=$9
MONGO_PORT=${10}
MONGO_DB=${11}

TEXTDATAPATH="$BASEDIR/text_data/${CORPUS}_sample/"
INPUTPATH="${BASEDIR}/data/${CORPUS}_sample/input/"
OUTPUTPATH="${BASEDIR}/data/${CORPUS}_sample/output/T${NUMTOPICS}/init/"

rm -r $TEXTDATAPATH
rm -r ${BASEDIR}/data/${CORPUS}_sample/

mkdir -p $TEXTDATAPATH
mkdir -p $INPUTPATH
mkdir -p $OUTPUTPATH

python $BASEDIR/scripts/filter_model_docs.py \
    $BASEDIR/data/$CORPUS/output/T${NUMTOPICS}/init/model.topics \
    $BASEDIR/data/${CORPUS}_sample/output/T${NUMTOPICS}/init/model.topics &

python $BASEDIR/scripts/downsample_by_topics.py \
    $BASEDIR/data/$CORPUS/output/T${NUMTOPICS}/init/model.docs \
    ${INPUTPATH}/posting_ids.pkl $DOWNSAMPLE_SIZE

python $BASEDIR/scripts/downsample_model_docs.py \
    ${INPUTPATH}/posting_ids.pkl \
    $BASEDIR/data/$CORPUS/output/T${NUMTOPICS}/init/model.docs \
    $BASEDIR/data/${CORPUS}_sample/output/T${NUMTOPICS}/init/model.docs 

python scripts/generate_html.py ${INPUTPATH}/posting_ids.pkl ${BASEDIR}/data/${CORPUS}_sample.html \
    ${MONGO_USER} ${MONGO_PW} ${MONGO_HOST} ${MONGO_PORT} ${MONGO_DB} &

python scripts/generate_titles.py ${INPUTPATH}/posting_ids.pkl ${BASEDIR}/data/${CORPUS}_sample.titles \
    ${MONGO_USER} ${MONGO_PW} ${MONGO_HOST} ${MONGO_PORT} ${MONGO_DB} &

python scripts/generate_url.py ${CORPUS}_sample ${INPUTPATH}/posting_ids.pkl ${INPUTPATH}/${CORPUS}_sample.url &

python scripts/generate_text_data.py ${INPUTPATH}/posting_ids.pkl ${TEXTDATAPATH} \
    ${MONGO_USER} ${MONGO_PW} ${MONGO_HOST} ${MONGO_PORT} ${MONGO_DB} 

bash $BASEDIR/scripts/import_mallet.sh ${CORPUS}_sample $BASEDIR $MALLET_HOME 

wait

