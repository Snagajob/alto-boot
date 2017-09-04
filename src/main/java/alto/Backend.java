package alto;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class Backend {

	public static String createNewSession(String username, HttpServletRequest req) throws ErrorForUI, IOException
	{
		String json="";
		System.out.println("Creating new session: corpus: " + util.Constants.CORPUS_NAME + ", username: " + username + ", topicsnum: " + util.Constants.NUM_TOPICS);
		TopicModeling treeTM = new TopicModeling(req);
		json = treeTM.changeFormat(req);
		return json;
	}
}
