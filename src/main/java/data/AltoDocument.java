package data;

import lombok.Data;

@Data
public class AltoDocument {
    private int id;
    private String text;

    public static AltoDocument create(int id, String text) {

        AltoDocument doc = new AltoDocument();
        doc.id = id;
        doc.text = text;

        return doc;
    }
}
