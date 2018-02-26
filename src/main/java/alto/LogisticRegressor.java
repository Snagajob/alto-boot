package alto;

import com.aliasi.corpus.ObjectHandler;
import com.aliasi.matrix.SparseFloatVector;
import com.aliasi.matrix.Vector;
import com.aliasi.stats.AnnealingSchedule;
import com.aliasi.stats.LogisticRegression;
import com.aliasi.stats.RegressionPrior;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.*;


@Component
public class LogisticRegressor  {

    @Value("${alto.data.corpus_name:synthetic}")
    private String corpusName;

    @Value("${alto.data.base_dir:/usr/local/alto-boot}")
    private String dataDirectory;

	private void fillTopicLabeledDocs(HashMap<String, Integer> topicLabeledDocs,
                                      TreeMap<String, String> idToLabelMap, int numTopics){
		for(int i = 0;i < numTopics; i++){
			topicLabeledDocs.put(i+"", 0);
		}
		for(String id:idToLabelMap.keySet()){
			String highestTopic = TopicModeling.docIdToHighestTopic.get(id).get(0);
			topicLabeledDocs.put(highestTopic, topicLabeledDocs.get(highestTopic)+1);
		}
	}
	private String outputModelFileName = "";

    public LogisticRegressor() {

    }

	public void init(HashMap<String, HashMap<Integer, Float>> features, TreeMap<String , String> idToLabelMap,
					 HashMap<String, Integer> labelStrToInt, ArrayList<String> testingIdList,
					 HashMap<String, String> testingIdToLabel, HashMap<String, ArrayList<Double>> idToProbs,
					 int numTopics, boolean isFromScratch, boolean isFirstTime,
                     Set<String> trainingLabelSet, String userName, HttpServletRequest req) {
		String outputModelFileName = this.dataDirectory + "/" + this.corpusName + "/output/T" +
				String.valueOf(numTopics) + "/init/" + this.corpusName + "_" + userName + "_model.saved";
		//create training set
		int trainingSize = idToLabelMap.keySet().size();
		Vector[] in = new Vector[trainingSize];
		int[] out = new int[trainingSize];
		int i = 0;
		HashMap<String, Integer> topicLabeledDocs = new HashMap<String, Integer>();
		fillTopicLabeledDocs(topicLabeledDocs, idToLabelMap, numTopics);
		for(String id: idToLabelMap.keySet()){
			HashMap<Integer, Float> indexToFeature = features.get(id);
			int maxIndex = Collections.max(indexToFeature.keySet());
			String highestTopic = TopicModeling.docIdToHighestTopic.get(id).get(0);
			int numLabeled = topicLabeledDocs.get(highestTopic);
			double featVal = (double)numLabeled/(double)20;
			indexToFeature.put(maxIndex, (float)featVal);
			SparseFloatVector f = new SparseFloatVector(indexToFeature, maxIndex+1);
			in[i] = f;
			String label = idToLabelMap.get(id);
			trainingLabelSet.add(label);
			out[i] = labelStrToInt.get(label);
			i++;
		}
		
		LogisticRegression model;
		
		ObjectHandler<LogisticRegression> handler = null;

		double initLearningRate=0.1;
		double minImprovement=0.01;
		int maxEpochs=1000;
		int rollingAverageSize=5;
		int minEpochs = 100;
		int blockSize = Math.max(1,out.length / 50);

		boolean canUseIncremental;
		LogisticRegression hotStart = null;
		if(!isFirstTime){
		    try {
                hotStart = readModel(outputModelFileName);
                // if the number of labels are the same in model and current training set, we can use incremental learning
                canUseIncremental = (hotStart.weightVectors().length == trainingLabelSet.size() - 1);
            } catch (IOException e) {
		        hotStart = null;
		        canUseIncremental = false;
		        e.printStackTrace();
            }
		}
		else{
			canUseIncremental = false;
		}
		if(!canUseIncremental || isFromScratch || isFirstTime){
			model = LogisticRegression.estimate(in, out,
					RegressionPrior.noninformative(),//prior
					blockSize,//Number of examples whose gradient is computed before updating coefficients.
					null,//hot start
					AnnealingSchedule.constant(initLearningRate),
					minImprovement, // min improve
					rollingAverageSize,//rolling avg size
					minEpochs, // min epochs
					maxEpochs,//max epochs
					handler, null); 
		
		}
		else{
			
			model = LogisticRegression.estimate(in, out,
					RegressionPrior.noninformative(),//prior
					blockSize,//Number of examples whose gradient is computed before updating coefficients.
					hotStart,
					AnnealingSchedule.constant(initLearningRate),
					minImprovement, // min improve
					rollingAverageSize,//rolling avg size
					minEpochs, // min epochs
					maxEpochs,//max epochs
					handler, null); 
			
		}
		try {
            writeModel(model, outputModelFileName);
        } catch (IOException e){
		    e.printStackTrace();
        }
		
		for(String testingId:testingIdList){
			//featurizing testing ids
			HashMap<Integer, Float> indexToFeature = features.get(testingId);
			int maxIndex = Collections.max(indexToFeature.keySet());
			String highestTopic = TopicModeling.docIdToHighestTopic.get(testingId).get(0);
			int numLabeled = topicLabeledDocs.get(highestTopic);
			double featVal = (double)numLabeled/(double)20;
			indexToFeature.put(maxIndex, (float)featVal);
			SparseFloatVector f = new SparseFloatVector(indexToFeature, maxIndex+1);
			
			double[] probs = model.classify(f);
			ArrayList<Double> probsObj = new ArrayList<Double>();
			for(i = 0 ; i < probs.length; i++){
				probsObj.add(probs[i]);
			}
			idToProbs.put(testingId, probsObj);
			Double maxProb = Collections.max(probsObj);
			int finalLabel = probsObj.indexOf(maxProb);
			for(String labelStr:labelStrToInt.keySet()){
				if(labelStrToInt.get(labelStr) == finalLabel)
					testingIdToLabel.put(testingId, labelStr);
			}
		}
	
	}
	
	public void writeModel(LogisticRegression lr, String modelFileName)
			throws IOException {

		FileOutputStream fileOut = new FileOutputStream(new File(modelFileName));
		BufferedOutputStream bufOut = new BufferedOutputStream(fileOut);
		ObjectOutputStream objOut = new ObjectOutputStream(bufOut);
		lr.compileTo(objOut);
		objOut.close();
		bufOut.close();
		fileOut.close();
	}
	public LogisticRegression readModel(String modelFileName) throws IOException{
		FileInputStream fin = new FileInputStream(modelFileName);
		ObjectInputStream ois = new ObjectInputStream(fin);
		LogisticRegression lr=null;
		try {
			lr = (LogisticRegression) ois.readObject();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		ois.close();
		return lr;
	}


}
