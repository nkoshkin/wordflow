package io.ylab.wordflow.repository;

import io.ylab.wordflow.entity.AnalysisEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AnalysisRepository extends JpaRepository<AnalysisEntity, UUID> {

    List<AnalysisEntity> findAllByOrderByStartTimeDesc();
}
