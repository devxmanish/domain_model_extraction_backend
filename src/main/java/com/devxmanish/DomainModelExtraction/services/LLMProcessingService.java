package com.devxmanish.DomainModelExtraction.services;

import com.devxmanish.DomainModelExtraction.models.Job;
import com.devxmanish.DomainModelExtraction.models.UserStory;
import java.util.List;

public interface LLMProcessingService {

    /** Process a single story via LLM */
    void processStory(Job job, UserStory story);

    /** Process multiple stories in batch */
    void processBatch(List<UserStory> stories);
}
