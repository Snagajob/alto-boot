package data.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.util.UUID;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(name="document_labels")
@IdClass(DocumentLabelPK.class)
public class DocumentLabel {

    @Id
    @NonNull
    @Column(name = "session_id")
    UUID sessionId;

    @Column(name="label_id")
    @NonNull
    int labelId;

    @Column(name="document_id")
    @NonNull
    String documentId;

    @Column(name="score")
    double score;

    @Column(name="confirmed")
    boolean confirmed;
}
