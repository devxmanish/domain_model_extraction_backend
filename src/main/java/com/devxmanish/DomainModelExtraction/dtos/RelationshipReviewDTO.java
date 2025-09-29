package com.devxmanish.DomainModelExtraction.dtos;

public record RelationshipReviewDTO(
        Long id,
        String source,
        String target,
        String type
) {
}
