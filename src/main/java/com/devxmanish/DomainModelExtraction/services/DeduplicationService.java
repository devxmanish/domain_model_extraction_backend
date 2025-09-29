package com.devxmanish.DomainModelExtraction.services;

public interface DeduplicationService {
    void deduplicateAndConsolidate(Long jobId);
    void transferToConfirmed(Long jobId); // for batch skipping dedupe
}
