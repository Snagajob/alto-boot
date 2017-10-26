package data;

import lombok.Data;

@Data
public class Document<TId> {
    private TId id;
    private String text;

    public static <TId> Document create(TId id, String text) {

        Document<TId> doc = new Document<>();
        doc.id = id;
        doc.text = text;

        return doc;
    }
}
