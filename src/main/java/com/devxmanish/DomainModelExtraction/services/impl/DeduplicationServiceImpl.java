package com.devxmanish.DomainModelExtraction.services.impl;

import com.devxmanish.DomainModelExtraction.dtos.DeduplicationLLMPayload;
import com.devxmanish.DomainModelExtraction.dtos.DeduplicationResult;
import com.devxmanish.DomainModelExtraction.enums.ExtractionPhase;
import com.devxmanish.DomainModelExtraction.exceptions.NotFoundException;
import com.devxmanish.DomainModelExtraction.models.*;
import com.devxmanish.DomainModelExtraction.repos.*;
import com.devxmanish.DomainModelExtraction.services.DeduplicationService;
import com.devxmanish.DomainModelExtraction.services.IntermediateReviewService;
import com.devxmanish.DomainModelExtraction.services.llm.LLMService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DeduplicationServiceImpl implements DeduplicationService {

    @Value("${deduplicationLLMModel}")
    private String llmModelName;

    @Autowired
    private IntermediateReviewService intermediateReviewService;

    @Autowired
    private ConfirmedClassRepository ccRepo;

    @Autowired
    private ConfirmedRelationshipRepository crRepo;

    @Autowired
    private LLMService llmService; // wrapper for LangChain4j

    @Autowired
    private JobRepository jobRepository;

    @Override
    public void deduplicateAndConsolidate(Long jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(()-> new NotFoundException("Job Not Found"));

//         1. Gather all intermediates (already CONFIRMED for STEP_BY_STEP, job-level for BATCH)
        List<IntermediateClass> classes = intermediateReviewService.getJobIntermediateClasses(jobId).stream()
                .filter(intermediateClass -> intermediateClass.getExtractionPhase() != ExtractionPhase.USER_DELETED)
                .toList();
        List<IntermediateRelationship> relationships = intermediateReviewService.getJobIntermediateRelationships(jobId).stream()
                .filter(intermediateRelationship -> intermediateRelationship.getExtractionPhase()!= ExtractionPhase.USER_DELETED)
                .toList();

        // 2. Build JSON payload
        DeduplicationLLMPayload payload = DeduplicationLLMPayload.from(classes, relationships);

        // 3. call llm
        String jsonResponse = llmService.consolidateDomainModel(jobId, payload, llmModelName);

        DeduplicationResult result = DeduplicationResult.fromJson(jsonResponse);

        // 4. Save into Confirmed tables
        persistConsolidated(job,result);
    }

    @Override
    public void transferToConfirmed(Long jobId) {
        // 1. Directly copy job-level intermediates into Confirmed tables (for batch skip-dedupe)
//        List<IntermediateClass> classes = icRepo.findByJobId(jobId);
//        List<IntermediateRelationship> relationships = irRepo.findByJobId(jobId);

//        classes.forEach(ic -> {
//            ConfirmedClass cc = new ConfirmedClass(jobId, ic.getClassName(),
//                    ic.getStoryIds(), ExtractionPhase.CONSOLIDATED);
//            ccRepo.save(cc);
//        });
//
//        relationships.forEach(ir -> {
//            ConfirmedRelationship cr = new ConfirmedRelationship(jobId,
//                    ir.getSourceClass().getClassName(),
//                    ir.getTargetClass().getClassName(),
//                    ir.getRelationshipType(),
//                    ir.getStoryIds(),
//                    ExtractionPhase.CONSOLIDATED);
//            crRepo.save(cr);
//        });
    }

    private void persistConsolidated(Job job, DeduplicationResult deduplicationResult) {
        log.info("Inside persistConsolidated()");

        // Persist confirmed classes and build a map: className -> ConfirmedClass
        Map<String, ConfirmedClass> classMap = deduplicationResult.getClasses().stream()
                .map(cr -> {
                    ConfirmedClass confirmedClass = ConfirmedClass.builder()
                            .job(job)
                            .className(cr.getName())
                            .storyIds(cr.getStoryIds() != null ? cr.getStoryIds().toString() : "[]") // directly store list
                            .extractionPhase(ExtractionPhase.CONSOLIDATED)
                            .build();
                    return ccRepo.save(confirmedClass); // save and return
                })
                .collect(Collectors.toMap(ConfirmedClass::getClassName, cc -> cc));

        // Persist confirmed relationships
        for (DeduplicationResult.RelationshipResult rel : deduplicationResult.getRelationships()) {
            ConfirmedClass sourceClass = classMap.get(rel.getSource());
            ConfirmedClass targetClass = classMap.get(rel.getTarget());

            if (sourceClass == null || targetClass == null) {
                log.warn("Warning: Class not found for relationship: {} -> {}", rel.getSource(), rel.getTarget());
                continue;
            }

            ConfirmedRelationship confirmedRelationship = ConfirmedRelationship.builder()
                    .job(job)
                    .sourceClass(sourceClass)
                    .targetClass(targetClass)
                    .relationshipType(rel.getType())
                    .storyIds(rel.getStoryIds() != null ? rel.getStoryIds().toString() : "[]") // store JSON as String
                    .extractionPhase(ExtractionPhase.CONSOLIDATED)
                    .build();

            crRepo.save(confirmedRelationship);
        }
    }


}
