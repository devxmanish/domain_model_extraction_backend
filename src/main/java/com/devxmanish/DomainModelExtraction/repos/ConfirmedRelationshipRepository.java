package com.devxmanish.DomainModelExtraction.repos;

import com.devxmanish.DomainModelExtraction.enums.ExtractionPhase;
import com.devxmanish.DomainModelExtraction.models.ConfirmedRelationship;
import com.devxmanish.DomainModelExtraction.models.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ConfirmedRelationshipRepository extends JpaRepository<ConfirmedRelationship, Long> {
    List<ConfirmedRelationship> findByJob(Job job);
    List<ConfirmedRelationship> findByJobId(Long jobId);
    List<ConfirmedRelationship> findByExtractionPhase(ExtractionPhase phase);
    List<ConfirmedRelationship> findBySourceClassIdAndTargetClassId(Long sourceClassId, Long targetClassId);
}
