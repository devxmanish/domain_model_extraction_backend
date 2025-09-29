package com.devxmanish.DomainModelExtraction.repos;

import com.devxmanish.DomainModelExtraction.enums.ExtractionPhase;
import com.devxmanish.DomainModelExtraction.models.IntermediateRelationship;
import com.devxmanish.DomainModelExtraction.models.UserStory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface IntermediateRelationshipRepository extends JpaRepository<IntermediateRelationship, Long> {
    List<IntermediateRelationship> findByStory(UserStory story);
    List<IntermediateRelationship> findByStoryId(Long storyId);
    List<IntermediateRelationship> findByJobId(Long storyId);
    List<IntermediateRelationship> findByStoryIdAndExtractionPhase(Long storyId, ExtractionPhase phase);
    List<IntermediateRelationship> findBySourceClassIdAndTargetClassId(Long sourceClassId, Long targetClassId);
}
