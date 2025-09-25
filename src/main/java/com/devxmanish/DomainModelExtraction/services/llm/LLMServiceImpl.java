package com.devxmanish.DomainModelExtraction.services.llm;

import com.devxmanish.DomainModelExtraction.dtos.LLMRelationshipDto;
import com.devxmanish.DomainModelExtraction.dtos.LLMResult;
import com.devxmanish.DomainModelExtraction.utility.PromptLoader;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class LLMServiceImpl implements LLMService {

    private final LLMSessionManager sessionManager;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public LLMServiceImpl(LLMSessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public LLMResult extractDomainModel(Long jobId, String modelName, String storyText) {
        log.info("Inside extractDomainModel()");

        String prompt = """
            You are a domain model extraction assistant.
            Extract classes and relationships from the following user story.
            Return ONLY valid JSON, no explanations, in the exact format:
            
            {
              "classes": ["Class1", "Class2", "..."],
              "relationships": [
                { "source": "Class1", "target": "Class2", "type": "relationshipType" }
              ]
            }
            
            User Story: "%s"
            """.formatted(storyText);

        // Use the session manager to maintain conversation memory per job
        String llmResponse = sessionManager.chat(jobId,modelName, prompt);

        // Convert LLM response to structured result
        return parseLLMResponse(llmResponse);
    }

    @Override
    public LLMResult extractDomainModelBatch(Long jobId, String modelName, List<String> stories) {
        log.info("Inside extractDomainModelBatch()");

        String prompt = """
            You are a domain model extraction assistant.
            Extract classes and relationships from the following list of user stories.
            Return ONLY valid JSON, no explanations, in the exact format:
            
            {
              "classes": ["Class1", "Class2", "..."],
              "relationships": [
                { "source": "Class1", "target": "Class2", "type": "relationshipType" },
                ....
              ]
            }
            
            User Stories: "%s"
            """.formatted(stories);

        String llmResponse = sessionManager.chat(jobId, modelName, prompt);

        return parseLLMResponse(llmResponse);
    }

    @Override
    public void closeJobSession(Long jobId) {
        log.info("Inside closeJobSession()");

        sessionManager.closeSession(jobId);
    }

    /**
     * Parse the LLM response text into a structured result (classes + relationships).
     * You can extend this to parse JSON or other structured LLM outputs.
     */
    private LLMResult parseLLMResponse(String responseText) {
        log.info("Inside parseLLMResponse()");

        try {
            JsonNode root = objectMapper.readTree(responseText);

            // Extract classes
            List<String> classes = new ArrayList<>();
            if (root.has("classes") && root.get("classes").isArray()) {
                root.get("classes").forEach(node -> classes.add(node.asText()));
            }

            // Extract relationships
            List<LLMRelationshipDto> relationships = new ArrayList<>();
            if (root.has("relationships") && root.get("relationships").isArray()) {
                for (JsonNode rel : root.get("relationships")) {
                    String source = rel.has("source") ? rel.get("source").asText() : null;
                    String target = rel.has("target") ? rel.get("target").asText() : null;
                    String type   = rel.has("type")   ? rel.get("type").asText()   : null;
                    relationships.add(new LLMRelationshipDto(source, target, type));
                }
            }

            return new LLMResult(classes, relationships);
        } catch (Exception e) {
            log.error("Failed to parse LLM response JSON", e);
            // Return empty but non-null result
            return new LLMResult(List.of(), List.of());
        }
    }
}
