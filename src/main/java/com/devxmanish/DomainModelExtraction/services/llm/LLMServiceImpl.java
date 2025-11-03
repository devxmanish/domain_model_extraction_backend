package com.devxmanish.DomainModelExtraction.services.llm;

import com.devxmanish.DomainModelExtraction.dtos.DeduplicationLLMPayload;
import com.devxmanish.DomainModelExtraction.dtos.DomainModelDTO;
import com.devxmanish.DomainModelExtraction.dtos.RelationshipDto;
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
    public DomainModelDTO extractDomainModel(Long jobId, String modelName, String storyText) {
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
    public DomainModelDTO extractDomainModelBatch(Long jobId, String modelName, List<String> stories) {
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

    @Override
    public String consolidateDomainModel(Long jobId, DeduplicationLLMPayload payload, String modelName) {
        log.info("Inside consolidateDomainModel()");

        String prompt = """
            You are a domain model consolidation assistant.
            Given classes and relationships (possibly with duplicates),
            produce a deduplicated consolidated domain model.
            Return ONLY valid JSON in this format:
            {
              "classes": [
                { "name": "User", "storyIds": [1, 2] },
                { "name": "Task", "storyIds": [1] }
              ],
              "relationships": [
                { "source": "User", "target": "Task", "type": "creates", "storyIds": [1] }
              ]
            }
            
            payload: "%s"
            """.formatted(payload);

        return sessionManager.chat(jobId,modelName, prompt);
    }

    @Override
    public String generatePlantUMLCode(Long jobId, DomainModelDTO payload, String modelName) {
        log.info("Inside generatePlantUMLCode()");

        String prompt = """
            You are a UML generation assistant.
            Given the final consolidated domain model in JSON format containing classes and relationships,
            generate the corresponding PlantUML class diagram code.
        
            Rules:
            1. Do NOT add or remove any classes or relationships from the payload.
            2. Preserve all names and relationship types exactly as given.
            3. For each class, add only the most likely core attributes 
               (like id, name, date, description, status, etc.) 
               that help to understand the domain easily — but keep them generic and minimal.
            4. Maintain clean and readable PlantUML syntax.
            5. Do NOT include any explanations, comments, or markdown — only output the PlantUML code.
        
            Expected Output Format:
            @startuml
            class User {
              +id
              +name
            }
            class Task {
              +id
              +title
              +status
            }
            User --> Task : creates
            @enduml
        
            payload: "%s"
            """.formatted(payload);


        return sessionManager.chat(jobId,modelName, prompt);
    }

    /**
     * Parse the LLM response text into a structured result (classes + relationships).
     * You can extend this to parse JSON or other structured LLM outputs.
     */
    private DomainModelDTO parseLLMResponse(String responseText) {
        log.info("Inside parseLLMResponse()");

        try {
            JsonNode root = objectMapper.readTree(responseText);

            // Extract classes
            List<String> classes = new ArrayList<>();
            if (root.has("classes") && root.get("classes").isArray()) {
                root.get("classes").forEach(node -> classes.add(node.asText()));
            }

            // Extract relationships
            List<RelationshipDto> relationships = new ArrayList<>();
            if (root.has("relationships") && root.get("relationships").isArray()) {
                for (JsonNode rel : root.get("relationships")) {
                    String source = rel.has("source") ? rel.get("source").asText() : null;
                    String target = rel.has("target") ? rel.get("target").asText() : null;
                    String type   = rel.has("type")   ? rel.get("type").asText()   : null;
                    relationships.add(new RelationshipDto(source, target, type));
                }
            }

            return new DomainModelDTO(classes, relationships);
        } catch (Exception e) {
            log.error("Failed to parse LLM response JSON", e);
            // Return empty but non-null result
            return new DomainModelDTO(List.of(), List.of());
        }
    }
}
