package io.ylab.wordflow.repository;

import io.ylab.wordflow.entity.WordCountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface WordCountRepository extends JpaRepository<WordCountEntity, Long> {

    List<WordCountEntity> findByAnalysisId(UUID analysisId);
}
