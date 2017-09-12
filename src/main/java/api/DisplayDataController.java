package api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

@Controller
@RequestMapping("/alto-release")
public class DisplayDataController {

    @Value("${alto.data.corpus_name:synthetic}")
    String corpusName;

    @Value("${alto.data.base_dir:/usr/local/alto-boot}")
    String dataDirectory;

	private Map<String, Integer> idToIndex = new HashMap<>();//maps doc id to the indec
	private List<String> texts = new ArrayList<>();//keeps doc texts that is displayed
	private List<String> ids = new ArrayList<>();//keeps doc ids

    @RequestMapping("DisplayData")
    public void displayDataRoute(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        boolean isLabelDocs = Boolean.parseBoolean(req.getParameter("isLabelDocs"));//TODO:send the parameter
        String backend = this.dataDirectory + "/" + this.corpusName + ".html";
        getData(backend);
        resp.setCharacterEncoding("UTF-8");
        String htmlStr = "";
        if(!isLabelDocs){
            String labelSetStr = req.getParameter("labelSet");
            htmlStr = getRelatedTexts(req.getParameter("docid"), Integer.parseInt(req.getParameter("numDisplayDocs")),
                    Boolean.parseBoolean(req.getParameter("newWindow")), Boolean.parseBoolean(req.getParameter("AL"))/*,topTopicWords*/, labelSetStr);
        }
        else{
            String labelDocIdsStr = req.getParameter("labelDocIds");
            ArrayList<String> labelDocIds = getLabelDocs(labelDocIdsStr);
            int startIndex = Integer.parseInt(req.getParameter("startIndex"));
            int endIndex = Integer.parseInt(req.getParameter("endIndex"));
            int numDocsPerPage = Integer.parseInt(req.getParameter("numDocsPerPage"));
            String labelName = req.getParameter("labelName");
            String labelSetStr = req.getParameter("labelSet");
            boolean isRefreshed = Boolean.parseBoolean(req.getParameter("isRefreshed"));
            htmlStr = getLabelDocsRelatedTexts(labelDocIds, startIndex, endIndex, numDocsPerPage, labelName,/* allDocsTopTopicWords,*/ labelSetStr, isRefreshed);
        }

        resp.setContentType("text/html");

        PrintWriter out = resp.getWriter();
        //String dir = util.Constants.RESULT_DIR;
        //out.print(getServletConfig().getServletContext().getRealPath("/"+dir));
        out.print(htmlStr);
        out.flush();
    }


	public ArrayList<String> getLabelDocs(String labelDocIdsStr){
		//gets a string of format docid:label,docid:label and returns a list of doc ids of documents having label x
		ArrayList<String> labelDocIds = new ArrayList<String>();
		String[] items = labelDocIdsStr.split(",");
		for (String id:items){
			labelDocIds.add(id);
		}
		return labelDocIds;
	}

	public void getData(String inputfile) throws IOException{
		//Reads in the html file and fills in data
		
        FileInputStream fis = null;
		BufferedReader br = null;

        try {
            fis = new FileInputStream(inputfile);
            br = new BufferedReader(new InputStreamReader(fis));

            String strLine;
            String id = "";
            while ((strLine = br.readLine()) != null) {
                strLine = strLine.trim();
                if (strLine.startsWith("<div class=\"segment\"")) {
                    int i = strLine.indexOf("id=");
                    id = strLine.substring(i + 4, strLine.length() - 2);
                    ids.add(id);
                    texts.add("");
                }
            }

            //sort ids based on the string
            Collections.sort(ids);
            for (int i = 0 ; i < ids.size(); i++){
                idToIndex.put(ids.get(i), i);
            }

            //TODO: why read the file again??
            br.close();
            fis.close();

            fis = new FileInputStream(inputfile);
            br = new BufferedReader(new InputStreamReader(fis));

            while ((strLine = br.readLine()) != null) {
                strLine = strLine.trim();
                if (strLine.startsWith("<div class=\"segment\"")) {
                    int i = strLine.indexOf("id=");
                    id = strLine.substring(i + 4, strLine.length() - 2);
                    int index = idToIndex.get(id);

                    String line = "";
                    String text = "";
                    while (!(line = br.readLine()).equals("</p>")) {
                        text += " " + line;
                    }
                    texts.set(index, text);
                }
            }
        }
        finally {
            br.close();
            fis.close();
        }

	}
	public String getLabelDocsRelatedTexts(ArrayList<String> labelDocIds, int startIndex, int endIndex, int numDocsPerPage, String labelName, 
			String labelSetStr, boolean isRefreshed){
		String htmlString = "";
		htmlString += "<html>\n";
		htmlString += "<head>\n";
		htmlString += "<script src=\"http://ajax.googleapis.com/ajax/libs/jquery/1.5/jquery.min.js\"></script>\n";
		htmlString += "<script type=\"text/javascript\" charset=\"utf-8\" src=\"static/js/Label.js\"></script>\n";
		htmlString += "<script type=\"text/javascript\" charset=\"utf-8\" src=\"static/js/main.js\"></script>\n";
		htmlString += "<script type=\"text/javascript\" charset=\"utf-8\" src=\"data/data.js\"></script>\n";
		htmlString += "<link rel=\"stylesheet\" href=\"data/data.css\">\n";
		htmlString += "</head>\n";
	
		htmlString += "<body onload=\"afterLoad("+numDocsPerPage+",'"+labelName+"','"+labelSetStr+"');mainWindow = window.opener.mainWindow; getLabelViewLoadTime("+isRefreshed+");\">\n";
		htmlString += "<form name=\"mainForm\">\n";
		htmlString += "<div align=\"center\" style=\"display:none\" id=\"main\" class=\"main\">\n";
		for(int i = 0; i < labelDocIds.size() ; i++){
			String docid = labelDocIds.get(i);
			htmlString += "<div class = \"segment\"   style=\"border:0px;\" id=\""+docid+"\">\n";
			htmlString += "<table width=\"100%\">";
			htmlString += "<tr><td>";
			htmlString += "<div id='top-part-"+docid+"'\" ></div>";
			htmlString += "</td></tr>";
			htmlString += "<tr style=\"outline: 0px solid\"><td width=\"100%\">";
			htmlString += "<div class = \"segment\" style=\"height:300px; overflow:scroll\" id=\""+docid+"\">";
			htmlString += "<table>";
		
			htmlString += "<tr><td>\n";
			int docIndex = idToIndex.get(docid);
			htmlString += texts.get(docIndex)+"\n";
			htmlString += "</p></td></tr>\n";
			htmlString += "</table>";
			htmlString += "</div>\n";
			

			htmlString += "<tr><td>";
			htmlString += "<div id=\"low-part-"+docid+"\"></div>";
			htmlString += "</td></tr>";
			htmlString += "</table>";
			if (i == labelDocIds.size()-1)
				htmlString += "<br /><br /><br /><br />";
			htmlString += "</div>\n";
		}
		//lower part table next and prev and close
		htmlString += "<table align=\"center\" border=\"0\" style=\"background-color:white; bottom:0px; right:-6px; position:fixed;\" width=\"100%\">";
		htmlString += "<tr> <td width=\"50%\" align=\"left\"><input type=\"button\" style=\"font-size:100%\" id=\"prevButton\" value=\"show prev 10\"";
		htmlString += "	onclick=\"load_prev_label_docs('/DisplayData?topic=Labels','"+labelName+"','"+startIndex+"','"+numDocsPerPage +"')\" />";
		htmlString += "</td>";
		htmlString += "<td width=\"50%\" align=\"right\"><input type=\"button\" style=\"font-size:100%\" id=\"nextButton\" value=\"show next 10\"";
		htmlString += "	onclick=\"load_next_label_docs('/DisplayData?topic=Labels','"+labelName+"','"+endIndex+"','"+numDocsPerPage +"')\" />";
		htmlString += "&nbsp; &nbsp; </td>";
		htmlString += "</tr>";

		htmlString += "<tr>";
		htmlString += "<td></td><td align=\"right\"> <input type=\"button\" onclick=\"closeWindowLabelView('"+labelName+"');\" style=\"font-size:100%\" value=\"close\">";
		htmlString += "&nbsp; &nbsp; </td></tr>";
		htmlString += "</table>";

		htmlString += "</form>\n";
		htmlString += "</body>\n";
		htmlString += "</html>\n";
		return htmlString;
	}

	public String getRelatedTexts(String id, int numDisplayDocs, boolean newWindow, boolean AL,/* ArrayList<String> topTopicWords,*/ String labelSetStr){
		//isSuggestDocs : is the document suggested from active learner
		//creates an html format string for docs to be displayed
		String htmlString = "";
		htmlString += "<html>\n";
		htmlString += "<head>\n";
		htmlString += "<script src=\"http://ajax.googleapis.com/ajax/libs/jquery/1.5/jquery.min.js\"></script>\n";
		htmlString += "<script type=\"text/javascript\" charset=\"utf-8\" src=\"static/js/Label.js\"></script>\n";
		htmlString += "<script type=\"text/javascript\" charset=\"utf-8\" src=\"static/js/main.js\"></script>\n";
		htmlString += "<script type=\"text/javascript\" charset=\"utf-8\" src=\"data/data.js\"></script>\n";
		htmlString += "<link rel=\"stylesheet\" href=\"data/data.css\">\n";
		htmlString += "</head>\n";
		htmlString += "<body onload=\"setTimeout(function(){addDocLabel("+numDisplayDocs+','+false+','+null+",'"+labelSetStr+"');getLoadTime();}, 100); mainWindow = window.opener.mainWindow;\">\n";

		htmlString += "<table width=\"100%\">";
		int idIndex = idToIndex.get((String)id);
		int startIndex=idIndex;
		int endIndex=idIndex;
		htmlString += "<tr><td>";
		htmlString += "<div id='top-part-"+id+"'\" ></div>";
		htmlString += "</td></td>";

		htmlString += "<tr><td width=\"100%\">";
		htmlString += "<form name=\"mainForm\">\n";
		htmlString += "<div style=\"display:none\" id=\"main\" class=\"main\">\n";
		for(int i = startIndex; i <= endIndex ; i++){
			htmlString += "<div class = \"segment\" style=\"height:300px; overflow:scroll;\" id =\""+ids.get(i)+"\">\n";
			htmlString += "<table>";
			htmlString += "</div><tr><td>\n";
			htmlString += texts.get(i)+ "\n";
			htmlString += "</p></td></tr>\n";
			htmlString += "</table>";
			htmlString += "</div>\n";
		}
		htmlString += "</div>\n";
		htmlString += "</td></tr>";

		htmlString += "<tr><td>";
		htmlString += "<div id=\"low-part-"+id+"\"></div></td></tr>";

		htmlString += "</table>";
		htmlString += "</form>\n";

		htmlString += "</body>\n";
		htmlString += "</html>\n";
		return htmlString;
	}


}
