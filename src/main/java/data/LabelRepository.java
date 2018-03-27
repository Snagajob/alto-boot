package data;

import java.util.List;
import java.util.UUID;

import data.entity.TaggingSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface LabelRepository extends JpaRepository<Label, Long>{
    List<Label> findByLabelSourceAndCorpus(
            Label.LabelCreationSource labelSource,
            Corpus corpus
    );

    List<Label> findByLabelSourceAndCorpusAndUser(
            Label.LabelCreationSource labelSource,
            Corpus corpus,
            User user
    );

    List<Label> findByCorpus_CorpusId(int corpusId);

    @Transactional
    int deleteBySession_SessionIdAndLabelName(UUID sessionId, String labelName);
}
