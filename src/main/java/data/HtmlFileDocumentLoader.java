package data;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;

@Component
public class HtmlFileDocumentLoader implements DocumentLoader {

    @Value("${alto.data.corpus_name:synthetic}")
    String corpusName;

    @Value("${alto.data.base_dir:/usr/local/alto-boot}")
    String dataDirectory;

    @Override
    public Map<Integer,Document<Integer>> getAllGroupedById() {
        String fileNameAndPath = this.dataDirectory + "/" + this.corpusName + ".html";
        return getData(fileNameAndPath);
    }


    public Map<Integer,Document<Integer>> getData(String fileNameAndPath) {
        //Reads in the html file and fills in data

        FileInputStream fis = null;
        BufferedReader br = null;

        try {
            fis = new FileInputStream(fileNameAndPath);
            br = new BufferedReader(new InputStreamReader(fis));

            String strLine;
            String id = "";
            Map<Integer, Document<Integer>> documents= new HashMap<>();

            while ((strLine = br.readLine()) != null) {
                strLine = strLine.trim();
                if (strLine.startsWith("<div class=\"segment\"")) {
                    int i = strLine.indexOf("id=");
                    id = strLine.substring(i + 4, strLine.length() - 2);
                    String documentText;
                    StringBuffer buf = new StringBuffer();

                    boolean accumulatedText = false;
                    while(!accumulatedText) {
                        documentText = br.readLine().trim();
                        if(documentText.equals("</div>")) {
                            accumulatedText = true;
                        }
                        else if(!documentText.equals("<p>") && !documentText.equals("</p>")) {
                            buf.append(documentText);
                        }
                    }

                    Document<Integer> d = Document.create(Integer.parseInt(id), buf.toString());
                    documents.put(d.getId(), d);
                }
            }

            return documents;
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
    }
}
