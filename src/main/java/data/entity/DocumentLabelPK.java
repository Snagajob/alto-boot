package data.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
public class DocumentLabelPK implements Serializable {
    UUID sessionId;
    int labelId;
    String documentId;

}
