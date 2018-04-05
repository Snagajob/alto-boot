package data.repository;

import data.entity.DocumentLabel;
import data.entity.DocumentLabelPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DocumentLabelsRepository extends JpaRepository<DocumentLabel,DocumentLabelPK> {
    List<DocumentLabel> findBySessionId(UUID sessionId);
}
