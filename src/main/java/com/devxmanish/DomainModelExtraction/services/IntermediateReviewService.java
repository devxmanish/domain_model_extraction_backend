package com.devxmanish.DomainModelExtraction.services;

import com.devxmanish.DomainModelExtraction.dtos.ClassReviewDTO;
import com.devxmanish.DomainModelExtraction.dtos.RelationshipReviewDTO;
import com.devxmanish.DomainModelExtraction.dtos.StoryReviewDTO;
import com.devxmanish.DomainModelExtraction.models.IntermediateClass;
import com.devxmanish.DomainModelExtraction.models.IntermediateRelationship;
import com.devxmanish.DomainModelExtraction.models.Job;
import com.devxmanish.DomainModelExtraction.models.UserStory;

import java.util.List;

public interface IntermediateReviewService {

    List<IntermediateClass> getStoryIntermediateClasses(Long storyId);
    List<IntermediateRelationship> getStoryIntermediateRelationships(Long storyId);

    // for STEP_BY_STEP flow
    List<StoryReviewDTO> getIntermediateByStoryForJob(Long jobId);

    List<IntermediateClass> getJobIntermediateClasses(Long jobId);
    List<IntermediateRelationship> getJobIntermediateRelationships(Long jobId);

    void addIntermediateClassForSBS(UserStory story, String className);
    void updateIntermediateClass(Long classId, String newName);
    void deleteIntermediateClass(Long classId);

    void addIntermediateRelationshipSBS(UserStory story, Long srcId, Long tgtId, String type);
    void updateIntermediateRelationship(Long relId, String newType);
    void deleteIntermediateRelationship(Long relId);

    void confirmStory(Long storyId);
    void confirmJob(Long jobId);

    void addIntermediateClassForBM(Job job, String className);

    void addIntermediateRelationshipBM(Job job, Long srcId, Long tgtId, String type);
}
