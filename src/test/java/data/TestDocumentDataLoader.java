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

        Map<Integer, AltoDocument> all = loader.getAllGroupedById();
        Assert.assertEquals(3, all.keySet().size());

        AltoDocument d0 = all.get(0);
        Assert.assertEquals("cat cat moose moose cat", d0.getText());
        Assert.assertEquals(0, (int)d0.getId());
    }

    @Test
    public void loadsMessyDocuments() throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("test-data/messy-1.html").getFile());
        String filePath = file.getAbsolutePath();

        HtmlFileDocumentLoader loader = new HtmlFileDocumentLoader();
        loader.dataDirectory = filePath.substring(0, filePath.lastIndexOf('/'));
        loader.corpusName = "messy-6";

        loader.init();
        Map<Integer, AltoDocument> allGroupedById = loader.getAllGroupedById();

        //Are they all there?
        Assert.assertEquals(6, allGroupedById.size());

        //Are they all correct(ish)?
        AltoDocument d0 = allGroupedById.get(40885851);
        AltoDocument d1 = allGroupedById.get(43962161);
        AltoDocument d2 = allGroupedById.get(41511219);
        AltoDocument d3 = allGroupedById.get(39473140);
        AltoDocument d4 = allGroupedById.get(43818866);
        AltoDocument d5 = allGroupedById.get(38273213);

        Assert.assertEquals(40885851, d0.getId());
        Assert.assertTrue(d0.getText().contains("<h2>Manager, Business Planning, Events and Recognition - Americas CALA (Integration)</h2>"));

        Assert.assertEquals(43962161, d1.getId());
        Assert.assertTrue(d1.getText().contains("<h2>Co-Manager - Collection at Riverpark #685</h2>"));

        Assert.assertEquals(41511219, d2.getId());
        Assert.assertTrue(d2.getText().contains("<h2>Grocery Night-Crew Clerk</h2>"));

        Assert.assertEquals(39473140, d3.getId());
        Assert.assertTrue(d3.getText().contains("<h2>Culinary - Banquets - Jr Sous Chef</h2>"));

        Assert.assertEquals(43818866, d4.getId());
        Assert.assertTrue(d4.getText().contains("http://media.snagajob.com/employer/images/profile/Staffmark/StaffmarkLG_hdr.png"));

        Assert.assertEquals(38273213, d5.getId());
        Assert.assertTrue(d5.getText().contains("<h2>Home Remodeler</h2>"));
    }
}
