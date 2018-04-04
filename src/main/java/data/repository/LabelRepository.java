package data.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import data.Corpus;
import data.Label;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface LabelRepository extends JpaRepository<Label, Long>{
    List<Label> findByLabelSourceAndCorpus(
            Label.LabelCreationSource labelSource,
            Corpus corpus
    );

    List<Label> findBySession_SessionId(UUID sessionId);

    Optional<Label> findBySession_SessionIdAndLabelName(UUID sessionId, String labelName);

    List<Label> findByLabelSourceAndSession_SessionIdAndCorpus_CorpusName(Label.LabelCreationSource labelSource, UUID sessionId, String corpusName);

    List<Label> findByCorpus_CorpusId(int corpusId);

    @Transactional
    int deleteBySession_SessionIdAndLabelName(UUID sessionId, String labelName);
}
