package alto;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.function.Supplier;

//gets a folder of data and outputs the feature file in following format:
// docid 1:count 2:count ...
//bag of words + topics

//@Component
public class Featurizer{
	
	public ArrayList<String> ids = new ArrayList<>();
	public TreeMap<String, String> idToTextMap = new TreeMap<>();
	public TreeMap<String, TreeMap<Integer, Integer>> idToTokenCounts = new TreeMap<>();
	public HashMap<String, TreeMap<Integer, Double>> docIdToTopicProb = new HashMap<>();
	public HashMap<String, Integer> vocabToIndex = new HashMap<>();
	public TreeMap<Integer, String> indexToVocab = new TreeMap<>();

        //@Value("${alto.data.base_dir:/usr/local/alto-boot}")
        String dataDirectory;

        //@Value("${alto.data.corpus_name:synthetic}")
        String corpusName;

        //@Value("${alto.data.source_text_dir}")
        String sourceTextDirectory;

        //@Value("${alto.data.num_topics:5}")
        int numTopics;

	public void featurize(String featuresDir, String dataDirectory, String corpusName, String sourceTextDir, int numTopics) throws IOException{
            this.dataDirectory = dataDirectory;
            this.corpusName = corpusName;
            this.sourceTextDirectory = sourceTextDir;
            this.numTopics = numTopics;

        //TODO: this should be done as a pre-processing step, so i'm leaving this code largely alone..
		if(util.Util.checkExist(featuresDir))
                    return;

		Writer writer = null;
		writer = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(featuresDir), "utf-8"));
		
		getData();
		fillVocab();
		if(ids.size() == 0){
			//getting doc ids
			String dir = this.sourceTextDirectory + "/" + corpusName;

			File folder = new File(dir);
			File[] listOfFiles = folder.listFiles();
			for(int i = 0 ; i < listOfFiles.length; i++){
                            
				//System.out.println("Featurizing file :"+listOfFiles[i].getName());
				ids.add(listOfFiles[i].getName());
				extractFeatures(listOfFiles[i].getName(), writer);
			}
		}
		writer.close();
	}

	public void fillVocab() throws IOException{
		FileInputStream infstream = new FileInputStream(dataDirectory+"/"+corpusName + "/input/"+corpusName+ ".voc");
		DataInputStream in = new DataInputStream(infstream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String strLine = "";
		int lineNum = 1;
		while((strLine= br.readLine()) != null){
			String[] items = strLine.split("\\s+");
			vocabToIndex.put(items[1], lineNum);
			indexToVocab.put(lineNum, items[1]);
			lineNum++;
		}
		br.close();
	}
	
	public void getData()
			throws IOException {
		// Reads doc id and doc text from file and fills in the map
		String dir = sourceTextDirectory + "/" + corpusName;
		File folder = new File(dir);
		File[] listOfFiles = folder.listFiles();
                System.out.println(dir);
                System.out.println(listOfFiles);

		if (idToTextMap.keySet().size() == 0) {
			idToTextMap = new TreeMap<String, String>();
			for(int i = 0 ; i < listOfFiles.length; i++){
				FileInputStream infstream = new FileInputStream(listOfFiles[i].getAbsolutePath());
				DataInputStream in = new DataInputStream(infstream);
				BufferedReader br = new BufferedReader(new InputStreamReader(in));
				String strLine;
				String id = listOfFiles[i].getName();
				String text = "";
				while ((strLine = br.readLine()) != null){
					text += strLine;
				}
				idToTextMap.put(id, text);
				br.close();
			}
		}
	}

	public void extractFeatures(String id, Writer writer) throws IOException{
		// gets an id and creates a map from vocab index to their count as a feature
		TreeMap<Integer, Integer> indexToFreqMap = new TreeMap<Integer, Integer>();
		String text = idToTextMap.get(id);
		StringTokenizer st = new StringTokenizer(text);
		while (st.hasMoreElements()) {
			String token = st.nextToken();
			token = token.toLowerCase();//lowercase everything
			if(!vocabToIndex.containsKey(token)) //|| stopWords.contains(token))
				continue;
			int vocabIndex = vocabToIndex.get(token);
	
			if (!indexToFreqMap.containsKey(vocabIndex))
				indexToFreqMap.put(vocabIndex, 1);
			else {
				int freq = indexToFreqMap.get(vocabIndex);
				indexToFreqMap.put(vocabIndex, freq + 1);
			}
		}
		//write to file
		int numWordsFeatures = 0;
		numWordsFeatures = vocabToIndex.keySet().size();

		fillDocIdToTopicProbMap(docIdToTopicProb);

		String line="";
		line = line + id + " ";
			for(int index:indexToFreqMap.keySet()){
				line = line + String.valueOf(index) + ":"
						+ String.valueOf(indexToFreqMap.get(index)) + " ";
			}
		
		String topicFeatStr = getTopicProbFeature(docIdToTopicProb, id , numWordsFeatures+1, numTopics);
		line += topicFeatStr;
		writer.write(line+"\n");
	}

	public void fillDocIdToTopicProbMap(HashMap<String, TreeMap<Integer, Double>> docIdToTopicProb) throws IOException{
		//reads in model.docs file and fills in id to topic prob map
		FileInputStream infstream = new FileInputStream(dataDirectory+"/"+corpusName+"/output/T"+
		numTopics+""+"/init/"+"model.docs");
		DataInputStream in = new DataInputStream(infstream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String strLine;
		br.readLine();//header
		while ((strLine = br.readLine()) != null){
			strLine = strLine.trim();
			String[] str = strLine.split("\\s+");
			String fullPath = str[1];
			String docId = fullPath.substring(fullPath.lastIndexOf("/")+1);
			int numtopics = (str.length-2) / 2;
			TreeMap<Integer, Double> topicToProb = new TreeMap<Integer, Double>();
			docIdToTopicProb.put(docId, topicToProb);
			for(int tt = 0; tt < numtopics; tt++) {
				int index = 2 * tt + 2;
				int topic = Integer.parseInt(str[index]);
				Double prob = Double.parseDouble(str[index+1]);

				docIdToTopicProb.get(docId).put(topic, prob);
			}
		}
		br.close();
	}
	public static String getTopicProbFeature(
			HashMap<String, TreeMap<Integer, Double>> docIdToTopicProb, String docId,
			int featureIndex, int numTopics) {
		//creates a line in the feature format for topic probs
		String line = "";
		for (int i = 0; i < numTopics; i++) {
			line += String.valueOf(featureIndex) + ":" + docIdToTopicProb.get(docId).get(i) + " ";
			featureIndex++;
		}
		return line;
	}

	public void writeFeaturesToFile(boolean ngrams, String dataFileName,int numTopics, String corpusName, HashMap<String, TreeMap<Integer, Double>> docIdToTopicProb)
			throws IOException {
		System.out.println("writing features to file...");

		fillDocIdToTopicProbMap(docIdToTopicProb);

		// Writing training file
		int numWordsFeatures = 0;
		if(ngrams)
			numWordsFeatures = vocabToIndex.keySet().size();
		else
			numWordsFeatures = 0;

		System.out.println("****Dimension = "+ numWordsFeatures);
		Writer writer = null;
		writer = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(dataFileName), "utf-8"));
		for (String id : ids) {
			System.out.println("Writing features to the file... "+ id);
			String line = "";

			line = line + id + " ";
			if (ngrams){
				for(int vocabIndex:indexToVocab.keySet()){
					if(idToTokenCounts.get(id).containsKey(vocabIndex)){
						line = line + String.valueOf(vocabIndex) + ":"
								+ String.valueOf(idToTokenCounts.get(id).get(vocabIndex)) + " ";
					}
					else{
						line = line + String.valueOf(vocabIndex) + ":"
								+ "0" + " ";
					}
				}
			}
			String topicFeatStr = getTopicProbFeature(docIdToTopicProb, id , numWordsFeatures+1, numTopics);
			line += topicFeatStr;
			writer.write(line+"\n");
		}
		writer.close();
		System.out.println("Finished writing features...");
	}
	public static HashMap<String, Integer> mapFeatureStrToInt(
			TreeMap<String, TreeMap<String, Integer>> featureMap, HashMap<Integer, String> featureIntToStr) {
		// maps a token feature from string to a unique integer starting from 1
		HashMap<String, Integer> featureStrToInt = new HashMap<String, Integer>();
		int featureInt = 0;
		for(String id: featureMap.keySet()){
			for (String featureStr : featureMap.get(id).keySet()) {
				if (!featureStrToInt.containsKey(featureStr)) {
					featureInt++;
					featureStrToInt.put(featureStr, featureInt);
					featureIntToStr.put(featureInt, featureStr);
				}
			}
		}

		return featureStrToInt;
	}
}
