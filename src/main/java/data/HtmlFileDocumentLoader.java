package data;

import java.io.File;
import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class HtmlFileDocumentLoader implements DocumentLoader {

    @Value("${alto.data.corpus_name:synthetic}")
    String corpusName;

    @Value("${alto.data.base_dir:/usr/local/alto-boot}")
    String dataDirectory;

    private String fileNameAndPath;

    @PostConstruct
    public void init() {
        this.fileNameAndPath = this.dataDirectory + "/" + this.corpusName + ".html";
    }

    @Override
    public Map<Integer, AltoDocument> getAllGroupedById() {
        File file = new File(fileNameAndPath);

        try {
            org.jsoup.nodes.Document d = Jsoup.parse(file, "UTF-8");

            Elements elementsByAttributeValueContaining = d.getElementsByAttributeValueContaining("class", "segment");

            return elementsByAttributeValueContaining.stream()
                    .map(e -> AltoDocument.create(Integer.parseInt(e.id()), e.html()))
                    .collect(Collectors.toMap(AltoDocument::getId, Function.identity()));


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
