package com.devxmanish.DomainModelExtraction.services;

import com.devxmanish.DomainModelExtraction.dtos.Response;
import com.devxmanish.DomainModelExtraction.dtos.ReviewDTO;
import com.devxmanish.DomainModelExtraction.models.*;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface DomainModelService {

    // === Review Phase ===
    Response<ReviewDTO> getReviewData(Long jobId);

    Response<?> confirmStory(Long storyId);   // STEP_BY_STEP only
    void confirmJob(Long jobId);       // BATCH only

    // === Deduplication & Consolidation ===
    Response<?> deduplicateAndConsolidate(Long jobId); // STEP_BY_STEP always, BATCH optionally
    void transferToConfirmed(Long jobId);       // BATCH only, skip dedupe

    // === Consolidated Review Phase ===
    Response<?> getConsolidatedReview(Long jobId);
    // === Confirmed Review Phase ===
    List<ConfirmedClass> getConfirmedClasses(Long jobId);
    List<ConfirmedRelationship> getConfirmedRelationships(Long jobId);


    Response<?> addIntermediateClassForSBS(Long storyId, String className);
    Response<?> updateIntermediateClass(Long classId, String className);
    Response<?> deleteIntermediateClass(Long classId);

    Response<?> addIntermediateRelationship(Long storyId, Long srcId, Long tgtId, String type);
    Response<?> updateIntermediateRelationship(Long relId, String newType);
    Response<?> deleteIntermediateRelationship(Long relId);


    Response<?> addConfirmedClass(Long jobId, String className);
    Response<?> updateConfirmedClass(Long classId, String newName);
    Response<?> deleteConfirmedClass(Long classId);

    Response<?> addConfirmedRelationship(Long jobId, Long srcId, Long tgtId, String type);
    Response<?> updateConfirmedRelationship(Long relId, String newType);
    Response<?> deleteConfirmedRelationship(Long relId);


    // === Finalization ===
    Response<?> getFinalDomainModel(Long jobId);

}
