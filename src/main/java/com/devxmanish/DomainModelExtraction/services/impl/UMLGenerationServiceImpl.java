package com.devxmanish.DomainModelExtraction.services.impl;

import com.devxmanish.DomainModelExtraction.dtos.DomainModelDTO;
import com.devxmanish.DomainModelExtraction.services.ConfirmedReviewService;
import com.devxmanish.DomainModelExtraction.services.UMLGenerationService;
import com.devxmanish.DomainModelExtraction.services.llm.LLMService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UMLGenerationServiceImpl implements UMLGenerationService {

    @Autowired
    private LLMService llmService;

    @Autowired
    private ConfirmedReviewService confirmedReviewService;

    @Value("${plantUmlLLMModel}")
    private String llmModelName;

    @Override
    public String generatePlantUML(Long jobId) {
        log.info("Inside generatePlantUML()");

        DomainModelDTO finalModel = confirmedReviewService.getFinalDomainModel(jobId);

        return llmService.generatePlantUMLCode(jobId, finalModel,llmModelName);
    }
}
