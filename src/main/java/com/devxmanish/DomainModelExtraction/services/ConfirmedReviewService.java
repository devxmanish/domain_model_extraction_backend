package com.devxmanish.DomainModelExtraction.services;

import com.devxmanish.DomainModelExtraction.dtos.ConsolidatedReviewDTO;
import com.devxmanish.DomainModelExtraction.dtos.DomainModelDTO;
import com.devxmanish.DomainModelExtraction.models.ConfirmedClass;
import com.devxmanish.DomainModelExtraction.models.ConfirmedRelationship;

import java.util.List;

public interface ConfirmedReviewService {

    List<ConfirmedClass> getConfirmedClasses(Long jobId);
    List<ConfirmedRelationship> getConfirmedRelationships(Long jobId);

    ConsolidatedReviewDTO getConsolidatedReview(Long jobId);

    void addConfirmedClass(Long jobId, String className);
    void updateConfirmedClass(Long classId, String newName);
    void deleteConfirmedClass(Long classId);

    void addConfirmedRelationship(Long jobId, Long srcId, Long tgtId, String type);
    void updateConfirmedRelationship(Long relId, String newType);
    void deleteConfirmedRelationship(Long relId);

    DomainModelDTO getFinalDomainModel(Long jobId);
}
