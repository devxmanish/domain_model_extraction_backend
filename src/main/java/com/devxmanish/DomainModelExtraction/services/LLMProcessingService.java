package com.devxmanish.DomainModelExtraction.services;

import com.devxmanish.DomainModelExtraction.models.Job;
import com.devxmanish.DomainModelExtraction.models.UserStory;
import java.util.List;

public interface LLMProcessingService {

    /** Process a single story via LLM */
    void processStory(String modelName, UserStory story);

    /** Process multiple stories in batch */
    void processBatch(String modelName, List<UserStory> stories);
}
