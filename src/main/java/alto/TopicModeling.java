package alto;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import util.*;
import util.GenerateVocab;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.*;


@Component
public class TopicModeling {

    @Value("${alto.data.corpus_name:synthetic}")
    String corpusName;

    @Value("${alto.data.base_dir:/usr/local/alto-boot}")
    String dataDirectory;

	@Value("${alto.data.words_per_topic:20}")
	int wordsNumPerTopic;

	@Value("${alto.data.top_docs_per_topic:20}")
	int docsNumPerTopic;

	String outputDir;
	String inputData;
	String outputName;
	String vocabFile;
	String featureFileDir;
	String urlFile;
	String summaryFile;
	String absOutputDir;
	
	public ArrayList<String> allInitialTopDocs = new ArrayList<>();//all of the top documents displayed in the beginning

	public static HashMap<String, HashMap<Integer, Float>> features;
	public static HashMap<String, ArrayList<String>> docIdToHighestTopic;
	public static HashMap<String, ArrayList<DocProb>> topicToDocs;
	public static HashMap<String, ArrayList<String>> highestDocs;//topic index to highest doc in that topic that is being displayed
    private String modelDocsFile;
    private String modelTopicWordsFile;
    private String shuffledTopDocsFile;

	public TopicModeling() {

	}

    @PostConstruct
    void init() throws IOException, ErrorForUI {
        features = new HashMap<>();
        docIdToHighestTopic = new HashMap<>();
        topicToDocs = new HashMap<>();
        highestDocs = new HashMap<>(); //topic index to highest doc in that topic that is being displayed

        String resultsBaseDir = String.format("%1$s/%2$s", this.dataDirectory, this.corpusName);
		String inputDir = String.format("%1$s/input", resultsBaseDir, this.corpusName);
		String absTopicDir = String.format("%1$s/output/T%2$s", resultsBaseDir, Constants.NUM_TOPICS);

        this.absOutputDir = absTopicDir + "/init";

        this.inputData = inputDir + "/" + this.corpusName + "-topic-input.mallet";//feeding in the input corpus in mallet format
        this.vocabFile = inputDir + "/" + this.corpusName + ".voc";
        this.urlFile = inputDir + "/" + this.corpusName + ".url";
        this.summaryFile = this.dataDirectory + "/" + this.corpusName + ".titles";
        this.outputName = this.outputDir + "/model";
		this.featureFileDir = this.absOutputDir + "/" + this.corpusName + ".feat";
        this.modelDocsFile = this.absOutputDir + "/" + "model.docs";
        this.modelTopicWordsFile = this.absOutputDir + "/" + "model.topics";
        this.shuffledTopDocsFile = this.absOutputDir + "/" + "shuffledTopDocs.txt";
        genVocab();
    }

	public void initializeData() throws IOException, NumberFormatException, ErrorForUI{
		loadFeatures(features);
		loadTopicProbs(docIdToHighestTopic, highestDocs, topicToDocs);
	}

	public class DocProb implements Comparable<Object> {
		public String id;
		public int wi;
		public double p;
		public DocProb (int wi, double p) { this.wi = wi; this.p = p;}
		public DocProb (String id, double p) { this.id = id; this.p = p;}
		public final int compareTo (Object o2) {
			if (p > ((DocProb)o2).p)
				return -1;
			else if (p == ((DocProb)o2).p)
				return 0;
			else return 1;
		}
	}
	class ValueComparator implements Comparator<String> {//comparator to sort a treemap based on values 
		HashMap<String, Double> base;
		public ValueComparator(HashMap<String, Double> base) {
			this.base = base;
		}

		public int compare(String a, String b) {
			if (base.get(a).equals(base.get(b))){
				return a.compareTo(b);
			}else if (base.get(a) > base.get(b)) {
				return -1;
			} else {
				return 1;
			} 
		}
	}

	public void genVocab() throws FileNotFoundException {
		if(Util.checkExist(this.vocabFile)){//if the input vocab file exists, use that. Otherwise generate vocab file
			System.out.println("Vocabulary file exists.");
			return;
		}
		//Generating vocab file
		String[] vocab_params = {//Using the training data, generates vocabulary based on vocab_params
				"--input", this.inputData,
				"--tfidf-thresh", "1",
				"--freq-thresh", "1",
				"--word-length", "1",
				"--tfidf-rank", "true",
				"--vocab", this.vocabFile};
		try {
			System.out.println("Generating vocab!");
			GenerateVocab.main(vocab_params);
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}
	
	public void rankDocs(ArrayList<String> fileList, HashMap<Integer, DocProb[]> topicDocsRanked, 
			HashMap<String, Integer> docToHighestTopic) throws ErrorForUI {

		HashMap<Integer, ArrayList<DocProb>> topicDocs = new HashMap<Integer, ArrayList<DocProb>> ();
        FileInputStream fis = null;
        BufferedReader bufferedReader = null;
        //TODO: This is file is read thru twice. Once here and once in the loadTopicProbs function.
		try{
            fis = new FileInputStream(this.modelDocsFile);
			bufferedReader = new BufferedReader(new InputStreamReader(fis));

			String strLine;
			int count = -1;
			while ((strLine = bufferedReader.readLine()) != null) {
				count++;
				if (count == 0) continue;

				strLine = strLine.trim();
				String[] str = strLine.split("\\s+");
				// id, doc, prob
				int id = Integer.parseInt(str[0]);
				//add high document topics
				String[] tmp = str[1].trim().split("/");
				String docRealId = tmp[tmp.length-1];
				int highestTopic = Integer.parseInt(str[2]);

				docToHighestTopic.put(docRealId, highestTopic);

				fileList.add(docRealId);

				int numtopics = (str.length-2) / 2;
				double[] probs = new double[numtopics];
				for(int tt = 0; tt < numtopics; tt++) {
					int index = 2 * tt + 2;
					int topic = Integer.parseInt(str[index]);
					probs[topic] = Double.parseDouble(str[index+1]);
				}
				for(int tt = 0; tt < probs.length; tt++) {
					ArrayList<DocProb> docs = null;
					if(!topicDocs.containsKey(tt)) {
						docs = new ArrayList<DocProb> ();
						topicDocs.put(tt, docs);
					} else {
						docs = topicDocs.get(tt);
					}
					if (probs[tt] > (double)1/(double)(Constants.NUM_TOPICS)) {//Forough changed from 0 to 0.1 to consider a doc to have a topic if prob > uniform
						DocProb doc = new DocProb(id, probs[tt]);
						docs.add(doc);
					}
				}
			}
		
		} catch (IOException e) {
			System.out.println("No model.docs file Found!");
			throw new ErrorForUI(e);
		}
		//topicDocs: maps a topic to all documents that have that topic with porb > uniform
		for(int topic : topicDocs.keySet()) {
			ArrayList<DocProb> array = topicDocs.get(topic);
			DocProb[] tmp = Arrays.copyOf(array.toArray(), array.size(), DocProb[].class);
			Arrays.sort(tmp);
			topicDocsRanked.put(topic, tmp);
		}

	}
	private HashMap<String, String> loadTopics () throws ErrorForUI {
		//reads in topic file and fills in the map from topics to its top words 
		HashMap<String, String> wordTopics = new HashMap<String, String> ();
        FileInputStream fis = null;
        BufferedReader bufferedReader = null;

		try {
            fis = new FileInputStream(this.modelTopicWordsFile);
			bufferedReader = new BufferedReader(new InputStreamReader(fis));
			String strLine;
		
			HashMap<Integer, HashMap<String, Double>> topicWordToWeights = new HashMap<Integer, HashMap<String, Double>>();
			for(int i = 0 ; i < Constants.NUM_TOPICS; i++){
				topicWordToWeights.put(i, new HashMap<>());
			}
			while ((strLine = bufferedReader.readLine()) != null) {
				strLine = strLine.trim();
				String[] items = strLine.split("\\s+");
				int topicIndex = Integer.parseInt(items[0]);
				String word = items[1];
				double weight = Double.parseDouble(items[2]);
				topicWordToWeights.get(topicIndex).put(word, weight);
			}
				for(int topicIndex: topicWordToWeights.keySet()){
					//normalize weights
					HashMap<String, Double> wordToWeights = topicWordToWeights.get(topicIndex);
					double weightSum = 0;
					for(String w:wordToWeights.keySet()){
						weightSum += wordToWeights.get(w);
					}
					for(String w : wordToWeights.keySet()){
						wordToWeights.put(w, wordToWeights.get(w)/weightSum);
					}
					//sort wordToWeights
					ValueComparator vc =  new ValueComparator(wordToWeights);
					TreeMap<String,Double> sortedWords = new TreeMap<String,Double>(vc);
					sortedWords.putAll(wordToWeights);
					String wordWeightsJson = "\"topic\": \"topic " + topicIndex + "\", ";
					wordWeightsJson += "\"topicindex\": " + topicIndex + ", ";

					wordWeightsJson += "\"words\": [";
					int cnt = 0;
					for(String w: sortedWords.keySet()){
						if(cnt < this.wordsNumPerTopic){
							wordWeightsJson += "{\"weight\": " + sortedWords.get(w) + ", \"word\": \"" + w + "\"}, ";
							cnt++;
						}
						else{
							break;
						}
					}
					wordWeightsJson += "]";
					wordWeightsJson = wordWeightsJson.replace("}, ]", "} ]");
					wordTopics.put(topicIndex+"",  wordWeightsJson);
				}
			
		} catch (IOException e) {
			System.out.println("No model.topics file found!");
			throw new ErrorForUI(e);
		}
        finally {
            try {
                bufferedReader.close();
                fis.close();
            }
            catch(Exception e) {
                throw new RuntimeException(e);
            }

        }
		return wordTopics;
	}

	//docTopics: maps topics to this.docsNumPerTopic top related docs
	private String loadDocs(HashMap<String, String> docTopics,
			HashMap<String, Integer> docToHighestTopic) throws ErrorForUI, IOException {
		// rank related documents for each topic
		ArrayList<String> fileList = new ArrayList<>();
		HashMap<Integer, DocProb[]> topicDocsRanked = new HashMap<>();
		this.rankDocs(fileList, topicDocsRanked, docToHighestTopic);

		String docJson = "\"documents\": [";
		for(int topic : topicDocsRanked.keySet()) {
			int count = 0;
			DocProb[] ranked = topicDocsRanked.get(topic);//get documents most related to the topic
			String docs = "\"docs\": [";
			for(int ii = 0; ii < ranked.length; ii++) {
				if (count >= this.docsNumPerTopic) {
					break;
				}
				int i = ranked[ii].wi;
				String name = fileList.get(i);

				docs += "\"" + name + "\", ";
				docJson += "{\"name\": \"" + name +"\"}, ";
				allInitialTopDocs.add(name);
				count += 1;
			}
			docs += "], ";
			docs = docs.replace(", ], ", "], ");
			docTopics.put(""+topic, docs);
		}
		docJson += "], ";
		docJson = docJson.replace("}, ],", "} ],");	
		Collections.shuffle(allInitialTopDocs);
		writeAllInitialTopDocsToFile();
		//read from file and fill in shuffledDocs
		String shuffledDocsJson = getShuffledDocsJson();
		docJson += shuffledDocsJson;
		docJson += "\"all_documents\": [";
		HashMap<String, String> idToSummary = new HashMap<String, String>();
		loadDocSummaries(idToSummary);
		for(int i = 0; i < fileList.size(); i++){
			String name = fileList.get(i);
			docJson += "{\"name\": \"" + name +"\",\"summary\":\""+idToSummary.get(name)+"\",\"highestTopic\":\""+docIdToHighestTopic.get(name).get(0)+"\"}, ";
		}

		docJson += "], ";
		docJson = docJson.replace("}, ],", "} ],");
	
		return docJson;
	}
	public void loadDocSummaries(HashMap<String, String> idToSummary) throws IOException {
		//String json = "\"doc_summaries\":[";

        FileInputStream fis = null;
		BufferedReader bufferedReader = null;

        try {
            fis = new FileInputStream(this.summaryFile);
            bufferedReader = new BufferedReader(new InputStreamReader(fis));
            String strLine;
            while ((strLine = bufferedReader.readLine()) != null) {
                String[] items = strLine.trim().split("#");
                idToSummary.put(items[0], items[1]);
            }
        }
        finally {
            fis.close();
            bufferedReader.close();
        }
	}
	public void writeAllInitialTopDocsToFile() throws ErrorForUI, IOException {
		// writes shuffled initial top docs to file for baseline to load
		String f = this.absOutputDir+"/shuffledTopDocs.txt";
		if(util.Util.checkExist(f))
			return;
		Writer writer = null;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(f), "utf-8"));
		} catch (IOException ex) {
			throw new ErrorForUI(ex);
		} 
		for(String id:allInitialTopDocs){
			writer.write(id+"\n");
		}
		writer.close();
	}
	public String getShuffledDocsJson() throws IOException{
		String json = "\"shuffledDocs\":[";
		String inputfile = this.outputDir+"/shuffledTopDocs.txt";

        FileInputStream fis = null;
		BufferedReader bufferedReader = null;

        try {
            fis = new FileInputStream(this.shuffledTopDocsFile);
            bufferedReader = new BufferedReader(new InputStreamReader(fis));
            String strLine;
            while ((strLine = bufferedReader.readLine()) != null) {
                json += "\"" + strLine.trim() + "\",";
            }
            json = json.substring(0, json.length() - 1);
            json += "],";
        }
        finally {
            fis.close();
            bufferedReader.close();
        }
		return json;
	}
	public String changeFormat() throws ErrorForUI, IOException{
		// read topics
		HashMap<String, String> wordTopics = this.loadTopics();

		// read documents
		HashMap<String, String> docTopics = new HashMap<>();
		HashMap<String, Integer> docToHighestTopic = new HashMap<>();

		String docJson = this.loadDocs(docTopics, docToHighestTopic);

		String topicJson = "\"topics\": [";
		for(String topic : wordTopics.keySet()) {
			topicJson += "{ " + docTopics.get(topic) + wordTopics.get(topic) + "}, ";
		}
		topicJson += "], ";
		topicJson = topicJson.replace("}, ], ", "} ], ");
		return  "{ " + docJson + topicJson + "\"corpusname\":\"" + this.corpusName +"\",\"topicsnum\":\""+ Constants.NUM_TOPICS+ "\"}";
	}

	public void loadTopicProbs(HashMap<String, ArrayList<String>> docIdToHighestTopic, HashMap<String, ArrayList<String>> highestDocs,
			HashMap<String, ArrayList<DocProb>> topicToDocs) throws NumberFormatException, IOException{
		//reads the model.doc file and loads doc to highest <topic,prob>
		for(int i = 0 ; i < Constants.NUM_TOPICS; i++){
			ArrayList<DocProb> docs = new ArrayList<>();
			topicToDocs.put(String.valueOf(i), docs);
		}

        BufferedReader breader = null;
        FileInputStream fis = null;

        try {
            fis = new FileInputStream(this.modelDocsFile);
            breader = new BufferedReader(new InputStreamReader(fis));

            String strLine;
            int count = -1;
            String[] tmp;
            String docRealId;
            String topicIndex;
            String topicProb;
            DocProb docObj;
            while ((strLine = breader.readLine()) != null) {
                count++;
                if (count == 0) continue;

                strLine = strLine.trim();

                String[] str = strLine.split("\\s+");
                // id, doc, prob
                //add high document topics
                tmp = str[1].trim().split("/");
                docRealId = tmp[tmp.length - 1];
                topicIndex = str[2];
                topicProb = str[3];
                docObj = new DocProb(docRealId, Double.parseDouble(topicProb));
                ArrayList<String> data = new ArrayList<String>();
                data.add(topicIndex);
                data.add(topicProb);
                docIdToHighestTopic.put(docRealId, data);
                topicToDocs.get(topicIndex).add(docObj);
            }
            // sort topic docs based on probability
            //String topicIndex;
            for (int i = 0; i < Constants.NUM_TOPICS; i++) {
                topicIndex = String.valueOf(i);
                ArrayList<DocProb> array = topicToDocs.get(topicIndex);
                Collections.sort(array);
                topicToDocs.put(topicIndex, array);
            }
            for (int i = 0; i < Constants.NUM_TOPICS; i++) {
                topicIndex = String.valueOf(i);
                ArrayList<String> topicHighestDocs = new ArrayList<String>();
                int k = 0;
                for (DocProb docObj1 : topicToDocs.get(topicIndex)) {//for all docs in that topic
                    String docId = docObj1.id;
                    if (k < Constants.TOPIC_DOC_NUM) {
                        topicHighestDocs.add(docId);
                        k++;
                    }
                }
                highestDocs.put(topicIndex, topicHighestDocs);
            }
        }
        finally {
            breader.close();
            fis.close();
        }
	}


	public void loadFeatures(HashMap<String, HashMap<Integer, Float>> features) throws ErrorForUI, NumberFormatException, IOException {
		Featurizer ff = new Featurizer();
		ff.featurize(this.featureFileDir);
		System.out.print("loading features...");


        FileInputStream fis = new FileInputStream(this.featureFileDir);
        BufferedReader br = new BufferedReader(new InputStreamReader(fis));

		String strLine;
		int numFeatures = 0;
		HashMap<Integer, Float> indexToFeature;
		String[] items; String id;String[] f;
		while((strLine = br.readLine()) != null){
			indexToFeature = new HashMap<Integer, Float>();
			items = strLine.split("\\s+", 2);
			id = items[0];
			f = items[1].split("\\s+");
			String[] data;
			int featureIndex = 0;
			for(String tuple : f){
				data = tuple.split(":");
				featureIndex = Integer.parseInt(data[0])-1;//-1 to start from 0
				float featureVal = Float.parseFloat(data[1]);
				indexToFeature.put(featureIndex, featureVal);
				numFeatures = featureIndex+1;//last index
			}

			//add zero feature for #labeled docs in the highest Topic
			indexToFeature.put(numFeatures, (float) 0.0);
			features.put(id, indexToFeature);
		}
		normalizeFeatures(features, numFeatures+1);
		br.close();
	}
	public void normalizeFeatures(HashMap<String, HashMap<Integer, Float>> features, int numFeatures) {
		double[] featMaxVal = new double[numFeatures];
		for (int i = 0; i < numFeatures; i++) {
			featMaxVal[i] = Double.NEGATIVE_INFINITY;
		}
		double[] featMinVal = new double[numFeatures];
		for (int i = 0; i < numFeatures; i++) {
			featMinVal[i] = Double.POSITIVE_INFINITY;
		}
		//SparseFloatVector f;
		HashMap<Integer, Float> indexToFeature;

		for (String id:features.keySet()) {//features for an example
			indexToFeature = features.get(id);
			for(int j : indexToFeature.keySet()){// for every feature
				double featVal = indexToFeature.get(j);
				if(featVal < featMinVal[j])
					featMinVal[j] = featVal;
				if(featVal > featMaxVal[j])
					featMaxVal[j] = featVal;
			}
		}
		//set the features that don't appear in some examples to 0
		for (String id:features.keySet()) {
			indexToFeature = features.get(id);
			for(int j = 0 ; j < numFeatures; j++){// for every feature
				if(!indexToFeature.containsKey(j)){
					featMinVal[j] = 0;
				}
			}
		}

		for (String id:features.keySet()) {//features for an example
			indexToFeature = features.get(id);

			for(int j : indexToFeature.keySet()){// for every feature
				double originalVal = indexToFeature.get(j);
				double newVal = 0;
				if(originalVal != 0)
					newVal = (originalVal - featMinVal[j])
					/ (featMaxVal[j]-featMinVal[j]);
				indexToFeature.put(j,(float)newVal);
			}
			features.put(id, indexToFeature);	
		}
	}
}
