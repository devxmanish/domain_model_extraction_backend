package com.devxmanish.DomainModelExtraction.services.llm;

import com.devxmanish.DomainModelExtraction.dtos.LLMResult;

import java.util.List;

public interface LLMService {

    /**
     * Extract domain model (classes + relationships) from a single story.
     * Maintains context per job (step-by-step mode).
     *
     * @param jobId     the ID of the job (session context)
     * @param storyText the user story text
     * @return LLMResult containing extracted classes and relationships
     */
    LLMResult extractDomainModel(Long jobId, String modelName, String storyText);

    /**
     * Extract domain models from a batch of stories (one-shot).
     *
     * @param jobId  the ID of the job
     * @param stories list of story texts
     * @return list of LLMResult, one per story
     */
    LLMResult extractDomainModelBatch(Long jobId, String modelName, List<String> stories);

    /**
     * Close the session/memory for a job after completion.
     *
     * @param jobId the ID of the job
     */
    void closeJobSession(Long jobId);
}
