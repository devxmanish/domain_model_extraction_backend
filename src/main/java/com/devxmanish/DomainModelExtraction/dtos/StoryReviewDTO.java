package com.devxmanish.DomainModelExtraction.dtos;

import com.devxmanish.DomainModelExtraction.enums.Status;
import com.devxmanish.DomainModelExtraction.models.IntermediateClass;
import com.devxmanish.DomainModelExtraction.models.IntermediateRelationship;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class StoryReviewDTO {
        private Long storyId;
        private String storyText;
        private Status status; // PENDING / CONFIRMED
        private List<ClassReviewDTO> intermediateClasses;
        private List<RelationshipReviewDTO> intermediateRelationships;
        // getters/setters
    }
    