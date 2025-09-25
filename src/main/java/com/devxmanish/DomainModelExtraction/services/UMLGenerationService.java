package com.devxmanish.DomainModelExtraction.services;

import com.devxmanish.DomainModelExtraction.models.ConfirmedClass;
import com.devxmanish.DomainModelExtraction.models.ConfirmedRelationship;

import java.util.List;

public interface UMLGenerationService {
    String generatePlantUML(Long jobId);
    String generatePlantUML(List<ConfirmedClass> classes, List<ConfirmedRelationship> relationships);
}
