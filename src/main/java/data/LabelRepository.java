package data;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LabelRepository extends JpaRepository<Label, Long>{
    List<Label> findByLabelName(String labelName);

    List<Label> findByCorpus_CorpusId(int corpusId);

}
