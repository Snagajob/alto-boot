package api;

import data.Document;
import data.DocumentLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/alto-boot")
public class DisplayDataController {

    @Value("${alto.data.corpus_name:synthetic}")
    String corpusName;

    @Value("${alto.data.base_dir:/usr/local/alto-boot}")
    String dataDirectory;

    @Autowired
    DocumentLoader docLoader;

	private Map<String, Integer> idToIndex = new HashMap<>();//maps doc id to the indec
	private List<String> texts = new ArrayList<>();//keeps doc texts that is displayed
	private List<String> ids = new ArrayList<>();//keeps doc ids
    private Map<Integer, Document> docs = new HashMap<>();

    private class LabelDoc {
        public String text;
        public String id;

        public LabelDoc(String id, String text) {
            this.text = text;
            this.id = id;
        }
    }

    @PostConstruct
    public void init() {
        String backend = this.dataDirectory + "/" + this.corpusName + ".html";
        DisplayData data = getData(backend);

        this.docs = docLoader.getAllGroupedById();

        this.idToIndex = data.idToIndex;
        this.texts = data.texts;
        this.ids = data.ids;
    }


    @RequestMapping("DisplayData")
    public String displayDataRoute(Integer docid, String labelSet, Integer numDisplayDocs, Boolean newWindow,
                                   Boolean AL, Map<String,Object> model) {

        model.put("message","hello there");
        model.put("numDisplayDocs", numDisplayDocs);
        model.put("labelSetStr", labelSet);


        model.put("idIndex", docid);
        model.put("documentText", this.docs.get(docid));
        model.put("id", docid);

        return "document_detail";
    }

    @RequestMapping("RelatedDocs")
    public String relatedDocsRoute(Boolean isLabelDocs, Integer docid, String labelSet, String labelDocIds, Integer startIndex,
                                   Integer endIndex, Integer numDisplayDocs, Boolean newWindow, Boolean AL, String isRefreshed,
                                   Map<String,Object> model) {

        ArrayList<String> labelDocs = getLabelDocs(labelDocIds);

        List<LabelDoc> docs = labelDocs.stream().map(x -> new LabelDoc(x, texts.get(Integer.parseInt(x))))
                .collect(Collectors.toList());

        model.put("labelDocs", docs);
        model.put("labelSetStr", labelSet);
        model.put("numDocsPerPage", numDisplayDocs);

        //TODO: temporary to catch "undefined" value coming from client.
        boolean refreshed = (isRefreshed != null) && (!isRefreshed.equals("undefined") && isRefreshed.equals("true"));

        model.put("isRefreshed", refreshed);

        return "related_documents";
    }

    @RequestMapping("DisplayData2")
    @ResponseBody()
    public String displayDataRoute(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        boolean isLabelDocs = Boolean.parseBoolean(req.getParameter("isLabelDocs"));//TODO:send the parameter

        resp.setCharacterEncoding("UTF-8");
        String htmlStr = "";
        if(!isLabelDocs){
            String labelSetStr = req.getParameter("labelSet");
            htmlStr = getDocumentDetails(req.getParameter("docid"), Integer.parseInt(req.getParameter("numDisplayDocs")),
                    Boolean.parseBoolean(req.getParameter("newWindow")), Boolean.parseBoolean(req.getParameter("AL"))/*,topTopicWords*/, labelSetStr);
        }else{
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

        return htmlStr;
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


	public DisplayData getData(String inputfile) {
		//Reads in the html file and fills in data
		List<String> texts = new ArrayList<>();//keeps doc texts that is displayed
		List<String> ids = new ArrayList<>();//keeps doc ids
        Map<String,Integer> idToIndex = new HashMap<>();

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
        catch(Exception ex) {
            throw new RuntimeException(ex);
        }
        finally {

            try {
                br.close();
                fis.close();
            }
            catch(Exception ex) {}
        }

        return new DisplayData(idToIndex, ids, texts);
	}



	public String getDocumentDetails(String id, int numDisplayDocs, boolean newWindow, boolean AL,/* ArrayList<String> topTopicWords,*/ String labelSetStr){
		//isSuggestDocs : is the document suggested from active learner
		//creates an html format string for docs to be displayed
        StringBuffer buf = new StringBuffer();

		buf.append("<html>\n");
		buf.append("<head>\n");
        buf.append("<script src=\"http://ajax.googleapis.com/ajax/libs/jquery/1.5/jquery.min.js\"></script>\n");
        buf.append("<script type=\"text/javascript\" charset=\"utf-8\" src=\"static/js/Label.js\"></script>\n");
        buf.append("<script type=\"text/javascript\" charset=\"utf-8\" src=\"static/js/main.js\"></script>\n");
        buf.append("<script type=\"text/javascript\" charset=\"utf-8\" src=\"data/data.js\"></script>\n");
        buf.append("<link rel=\"stylesheet\" href=\"data/data.css\">\n");
        buf.append("</head>");
        buf.append("<body onload=\"setTimeout(function(){addDocLabel("+numDisplayDocs+','+false+','+null+",'"+labelSetStr+"');getLoadTime();}, 100); mainWindow = window.opener.mainWindow;\">\n");
        buf.append("<table width=\"100%\">");
        buf.append("<tr><td>");
        buf.append("<div id='top-part-"+id+"'\" ></div>");
        buf.append("</td></td>");
        buf.append("<tr><td width=\"100%\">");
        buf.append("<form name=\"mainForm\">\n");
        buf.append("<div style=\"display:none\" id=\"main\" class=\"main\">\n");

        int idIndex = idToIndex.get(id);
        buf.append("<div class = \"segment\" style=\"height:300px; overflow:scroll;\" id =\""+ids.get(idIndex)+"\">\n");


        buf.append("<table>");
        buf.append("</div><tr><td>\n");
        buf.append(texts.get(idIndex)+ "\n");
        buf.append("</p></td></tr>\n");
        buf.append("</table>");
        buf.append("</div>\n");
        buf.append("</div>\n");
        buf.append("</td></tr>");

        buf.append("<tr><td>");
        buf.append("<div id=\"low-part-"+id+"\"></div></td></tr>");

        buf.append("</table>");
        buf.append("</form>\n");

        buf.append("</body>\n");
        buf.append("</html>\n");

		return buf.toString();
	}

    public String getLabelDocsRelatedTexts(ArrayList<String> labelDocIds, int startIndex, int endIndex, int numDocsPerPage, String labelName,
                                           String labelSetStr, boolean isRefreshed){
        StringBuffer buf = new StringBuffer();
        buf.append( "<html>\n");
        buf.append( "<head>\n");
        buf.append( "<script src=\"http://ajax.googleapis.com/ajax/libs/jquery/1.5/jquery.min.js\"></script>\n");
        buf.append( "<script type=\"text/javascript\" charset=\"utf-8\" src=\"static/js/Label.js\"></script>\n");
        buf.append( "<script type=\"text/javascript\" charset=\"utf-8\" src=\"static/js/main.js\"></script>\n");
        buf.append( "<script type=\"text/javascript\" charset=\"utf-8\" src=\"data/data.js\"></script>\n");
        buf.append( "<link rel=\"stylesheet\" href=\"data/data.css\">\n");
        buf.append( "</head>\n");

        buf.append( "<body onload=\"afterLoad("+numDocsPerPage+",'"+labelName+"','"+labelSetStr+"');mainWindow = window.opener.mainWindow; getLabelViewLoadTime("+isRefreshed+");\">\n");
        buf.append( "<form name=\"mainForm\">\n");
        buf.append( "<div align=\"center\" style=\"display:none\" id=\"main\" class=\"main\">\n");
        for(int i = 0; i < labelDocIds.size() ; i++){
            String docid = labelDocIds.get(i);
            buf.append( "<div class = \"segment\"   style=\"border:0px;\" id=\""+docid+"\">\n");
            buf.append( "<table width=\"100%\">");
            buf.append( "<tr><td>");
            buf.append( "<div id='top-part-"+docid+"'\" ></div>");
            buf.append( "</td></tr>");
            buf.append( "<tr style=\"outline: 0px solid\"><td width=\"100%\">");
            buf.append( "<div class = \"segment\" style=\"height:300px; overflow:scroll\" id=\""+docid+"\">");
            buf.append( "<table>");

            buf.append( "<tr><td>\n");
            int docIndex = idToIndex.get(docid);
            buf.append( texts.get(docIndex)+"\n");
            buf.append( "</p></td></tr>\n");
            buf.append( "</table>");
            buf.append( "</div>\n");


            buf.append( "<tr><td>");
            buf.append( "<div id=\"low-part-"+docid+"\"></div>");
            buf.append( "</td></tr>");
            buf.append( "</table>");
            if (i == labelDocIds.size()-1)
                buf.append( "<br /><br /><br /><br />");
            buf.append( "</div>\n");
        }
        //lower part table next and prev and close
        buf.append( "<table align=\"center\" border=\"0\" style=\"background-color:white; bottom:0px; right:-6px; position:fixed;\" width=\"100%\">");
        buf.append( "<tr> <td width=\"50%\" align=\"left\"><input type=\"button\" style=\"font-size:100%\" id=\"prevButton\" value=\"show prev 10\"");
        buf.append( "	onclick=\"load_prev_label_docs('/DisplayData?topic=Labels','"+labelName+"','"+startIndex+"','"+numDocsPerPage +"')\" />");
        buf.append( "</td>");
        buf.append( "<td width=\"50%\" align=\"right\"><input type=\"button\" style=\"font-size:100%\" id=\"nextButton\" value=\"show next 10\"");
        buf.append( "	onclick=\"load_next_label_docs('/DisplayData?topic=Labels','"+labelName+"','"+endIndex+"','"+numDocsPerPage +"')\" />");
        buf.append( "&nbsp; &nbsp; </td>");
        buf.append( "</tr>");

        buf.append( "<tr>");
        buf.append( "<td></td><td align=\"right\"> <input type=\"button\" onclick=\"closeWindowLabelView('"+labelName+"');\" style=\"font-size:100%\" value=\"close\">");
        buf.append( "&nbsp; &nbsp; </td></tr>");
        buf.append( "</table>");

        buf.append( "</form>\n");
        buf.append( "</body>\n");
        buf.append( "</html>\n");

        return buf.toString();
    }

	private class DisplayData {
        public final Map<String, Integer> idToIndex = new HashMap<>();//maps doc id to the indec
        public final List<String> texts = new ArrayList<>();//keeps doc texts that is displayed
        public final List<String> ids = new ArrayList<>();//keeps doc ids

        public DisplayData(Map<String, Integer> idToIndex, List<String> ids, List<String> texts) {
            this.idToIndex.putAll(idToIndex);
            this.texts.addAll(texts);
            this.ids.addAll(ids);
        }
    }

}
