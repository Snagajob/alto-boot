package data;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
@Table(name="labels")
public class Label implements Serializable {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="label_id")
    private int labelId;

    @NotNull
    @Column(name="label_name")
    private String labelName;

    @NotNull
    @ManyToOne
    @JoinColumn(name="corpus_id")
    private Corpus corpus;

    @Enumerated(EnumType.STRING)
    @Column(name="label_source")
    private LabelCreationSource labelCreationSource;

}
