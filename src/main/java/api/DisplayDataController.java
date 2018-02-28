package api;

import data.AltoDocument;
import data.DocumentLoader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.PostConstruct;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/alto-boot")
public class DisplayDataController {

    @Autowired
    DocumentLoader docLoader;

    private Map<Integer, AltoDocument> docs = new HashMap<>();

    @PostConstruct
    public void init() {
        this.docs = docLoader.getAllGroupedById();
    }


    @RequestMapping("DisplayData")
    public String displayDataRoute(Integer docid, String labelSet, Integer numDisplayDocs, Boolean newWindow,
                                   Boolean AL, Map<String,Object> model) {

        model.put("numDisplayDocs", numDisplayDocs);
        model.put("labelSetStr", labelSet);

        model.put("document", this.docs.get(docid));

        return "document_detail";
    }

    @RequestMapping("RelatedDocs")
    public String relatedDocsRoute(String labelSet, String labelDocIds, String labelName,
                                   Integer startIndex, Integer endIndex, Integer numDisplayDocs,
                                   String isRefreshed, Map<String,Object> model) {

        List<AltoDocument> collect = Arrays.stream(labelDocIds.split(","))
                .map(Integer::parseInt)
                .map(this.docs::get)
                .collect(Collectors.toList());

        model.put("labelName", labelName);
        model.put("labelDocs", collect);
        model.put("labelSetStr", labelSet);
        model.put("numDocsPerPage", numDisplayDocs);
        model.put("startIndex", startIndex);
        model.put("endIndex", endIndex);

        //TODO: temporary to catch "undefined" value coming from client.
        boolean refreshed = (isRefreshed != null) && (!isRefreshed.equals("undefined") && isRefreshed.equals("true"));

        model.put("isRefreshed", refreshed);

        return "related_documents";
    }
}
