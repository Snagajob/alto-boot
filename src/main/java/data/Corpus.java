package data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
@Table(name="corpora")
public class Corpus {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="corpus_id")
    private int corpusId;

    @NotNull
    @Column(name="corpus_name")
    private String corpusName;

}
