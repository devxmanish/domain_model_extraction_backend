package com.devxmanish.DomainModelExtraction.dtos;

import com.devxmanish.DomainModelExtraction.enums.JobType;
import com.devxmanish.DomainModelExtraction.enums.Status;

import java.time.LocalDateTime;

public record JobDTO(
        Long id,
        JobType jobType,
        String model,
        Integer totalStories,
        Integer processedStories,
        Status status,
        LocalDateTime createdAt,
        LocalDateTime endAt
) {
}
