package com.devxmanish.DomainModelExtraction.services;

import com.devxmanish.DomainModelExtraction.models.ConfirmedClass;
import com.devxmanish.DomainModelExtraction.models.ConfirmedRelationship;
import com.devxmanish.DomainModelExtraction.models.IntermediateClass;
import com.devxmanish.DomainModelExtraction.models.IntermediateRelationship;

import java.util.List;

public interface DomainModelService {
    // Story-by-story review
    List<IntermediateClass> getIntermediateClasses(Long storyId);
    List<IntermediateRelationship> getIntermediateRelationships(Long storyId);
    List<IntermediateClass> getConfirmedSoFarClasses(Long jobId);
    List<IntermediateRelationship> getConfirmedSoFarRelationships(Long jobId);

    // Edit operations
    IntermediateClass addIntermediateClass(Long storyId, String className);
    IntermediateClass updateIntermediateClass(Long classId, String newName);
    void deleteIntermediateClass(Long classId);

    IntermediateRelationship addIntermediateRelationship(Long storyId, Long sourceId, Long targetId, String type);
    IntermediateRelationship updateIntermediateRelationship(Long relationshipId, String newType);
    void deleteIntermediateRelationship(Long relationshipId);

    // Confirm story
    void confirmStory(Long storyId);

    // Consolidation + deduplication
    void consolidateJob(Long jobId);
    List<ConfirmedClass> getConfirmedClasses(Long jobId);
    List<ConfirmedRelationship> getConfirmedRelationships(Long jobId);
}
