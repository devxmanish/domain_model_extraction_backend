package com.devxmanish.DomainModelExtraction.services.llm;

import com.devxmanish.DomainModelExtraction.dtos.DeduplicationLLMPayload;
import com.devxmanish.DomainModelExtraction.dtos.DomainModelDTO;

import java.util.List;

public interface LLMService {

    /**
     * Extract domain model (classes + relationships) from a single story.
     * Maintains context per job (step-by-step mode).
     *
     * @param jobId     the ID of the job (session context)
     * @param storyText the user story text
     * @return DomainModelDTO containing extracted classes and relationships
     */
    DomainModelDTO extractDomainModel(Long jobId, String modelName, String storyText);

    /**
     * Extract domain models from a batch of stories (one-shot).
     *
     * @param jobId  the ID of the job
     * @param stories list of story texts
     * @return list of DomainModelDTO, one per story
     */
    DomainModelDTO extractDomainModelBatch(Long jobId, String modelName, List<String> stories);

    /**
     * Close the session/memory for a job after completion.
     *
     * @param jobId the ID of the job
     */
    void closeJobSession(Long jobId);

    String consolidateDomainModel(Long jobId, DeduplicationLLMPayload payload, String modelName);
}
