package io.ylab.wordflow.repository;

import io.ylab.wordflow.entity.ErrorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ErrorRepository extends JpaRepository<ErrorEntity, Long> {

    List<ErrorEntity> findByAnalysisId(UUID analysisId);
}
