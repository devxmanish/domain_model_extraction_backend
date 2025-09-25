package com.devxmanish.DomainModelExtraction.services.impl;

import com.devxmanish.DomainModelExtraction.dtos.LLMRelationshipDto;
import com.devxmanish.DomainModelExtraction.dtos.LLMResult;
import com.devxmanish.DomainModelExtraction.enums.ExtractionPhase;
import com.devxmanish.DomainModelExtraction.models.IntermediateClass;
import com.devxmanish.DomainModelExtraction.models.IntermediateRelationship;
import com.devxmanish.DomainModelExtraction.models.Job;
import com.devxmanish.DomainModelExtraction.models.UserStory;
import com.devxmanish.DomainModelExtraction.repos.IntermediateClassRepository;
import com.devxmanish.DomainModelExtraction.repos.IntermediateRelationshipRepository;
import com.devxmanish.DomainModelExtraction.services.LLMProcessingService;
import com.devxmanish.DomainModelExtraction.services.llm.LLMService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class LLMProcessingServiceImpl implements LLMProcessingService {

    @Autowired
    private LLMService llmService;

    @Autowired
    private IntermediateClassRepository classRepo;

    @Autowired
    private IntermediateRelationshipRepository relRepo;

    /** Step-by-step story processing */
    @Override
    public void processStory(String modelName, UserStory story) {
        log.info("Inside processStory() STEP_BY_STEP");

        LLMResult result = llmService.extractDomainModel(story.getJob().getId(), modelName, story.getStoryText());
        persistIntermediate(story.getJob(), story, result);
    }

    /** Batch processing */
    @Override
    public void processBatch(String modelName, List<UserStory> stories) {
        log.info("Inside processBatch() STEP_BY_STEP");

        if (stories.isEmpty()) return;

        List<String> texts = stories.stream()
                .map(UserStory::getStoryText)
                .toList();
        LLMResult result = llmService.extractDomainModelBatch(stories.getFirst().getJob().getId(), modelName, texts);

        // here we  are storing the first story id of the job in case of batch processing
        persistIntermediate(stories.getFirst().getJob(),stories.getFirst(), result);
    }

    /** Persist intermediate classes and relationships */
    private void persistIntermediate(Job job, UserStory story, LLMResult result) {
        log.info("Inside persistIntermediate()");

        // Persist classes and keep a map: className -> IntermediateClass
        Map<String, IntermediateClass>  classMap = result.classes().stream()
                .map(className -> new IntermediateClass(job, story, className, ExtractionPhase.LLM))
                .map(classRepo::save) // save and return saved entity with ID
                .collect(Collectors.toMap(IntermediateClass::getClassName, ic -> ic));

        // Persist relationships using resolved IDs
        for (LLMRelationshipDto rel : result.relationships()) {
            IntermediateClass sourceClass = classMap.get(rel.source());
            IntermediateClass targetClass = classMap.get(rel.target());

            if (sourceClass == null || targetClass == null) {
                // Optional: handle missing classes
                System.out.printf("Warning: Class not found for relationship: %s -> %s%n",
                        rel.source(), rel.target());
                continue;
            }

            IntermediateRelationship ir = new IntermediateRelationship(
                    job,
                    story,
                    sourceClass,
                    targetClass,
                    rel.type(),
                    ExtractionPhase.LLM
            );
            relRepo.save(ir);
        }
    }
}
