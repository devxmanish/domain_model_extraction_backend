package com.devxmanish.DomainModelExtraction.dtos;

import com.devxmanish.DomainModelExtraction.models.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewDTO {

    private Long jobId;
    private String jobType; // STEP_BY_STEP or BATCH

    // For STEP_BY_STEP
    private List<StoryReviewDTO> stories; // each story with intermediates

    // For BATCH
    private List<ClassReviewDTO> jobIntermediateClasses;
    private List<RelationshipReviewDTO> jobIntermediateRelationships;

    // ===== Factory Methods =====

    public static ReviewDTO fromStepByStep(Job job,
                                           List<StoryReviewDTO> stories) {
        ReviewDTO dto = new ReviewDTO();
        dto.jobId = job.getId();
        dto.jobType = job.getJobType().name();
        dto.stories = stories;
        return dto;
    }

    public static ReviewDTO fromBatch(Job job,
                                      List<ClassReviewDTO> jobClasses,
                                      List<RelationshipReviewDTO> jobRels) {
        ReviewDTO dto = new ReviewDTO();
        dto.jobId = job.getId();
        dto.jobType = job.getJobType().name();
        dto.jobIntermediateClasses = jobClasses;
        dto.jobIntermediateRelationships = jobRels;
        return dto;
    }

    // getters/setters
}
