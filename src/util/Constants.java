package util;


public class Constants
{
	public static final String CORPUS_NAME = "postings_x";
//	public static final String CORPUS_NAME = "synthetic";
	public static final String ABS_BASE_DIR = "/Users/robert.mealey/ALTO-ACL-2016/WebContent/";
	public static final String RESULT_DIR = "results/";
	public static final String TEXT_DATA_DIR = "/Users/robert.mealey/ALTO-ACL-2016/text_data/postings_x/";
//	public static final String TEXT_DATA_DIR = "/Users/robert.mealey/ALTO-ACL-2016/text_data/synthetic/";
	public static final int NUM_TOPICS = 50;
//	public static final int NUM_TOPICS = 5;
	//Classification 
	public static final int NUM_TOP_DOCS = 100; // assign labels to this many documents in each label
	//UI
	public final static int TOPIC_DOC_NUM = 20; // the number of top documents for each topic
	public final static int TOPIC_WORD_NUM = 20; // number of words displayed for each topic
}
