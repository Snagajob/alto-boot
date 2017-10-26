package data;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.Map;

public class TestDocumentDataLoader {

    @Test
    public void threeDocumentsAreReturned() throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("test-data/synthetic-3.html").getFile());
        String filePath = file.getAbsolutePath();

        HtmlFileDocumentLoader loader = new HtmlFileDocumentLoader();
        loader.dataDirectory = filePath.substring(0, filePath.lastIndexOf('/'));
        loader.corpusName = "synthetic-3";

        Map<Integer, Document<Integer>> all = loader.getAllGroupedById();
        Assert.assertEquals(3, all.keySet().size());

        Document<Integer> d0 = all.get(0);
        Assert.assertEquals("cat cat moose moose cat", d0.getText());
        Assert.assertEquals(0, (int)d0.getId());
    }
}
