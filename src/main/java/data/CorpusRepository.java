package data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CorpusRepository extends JpaRepository<Corpus, Long>{
    Optional<Corpus> findByCorpusName(String corpusName);
}
