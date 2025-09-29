package com.devxmanish.DomainModelExtraction.dtos;

import lombok.Builder;

import java.util.List;

@Builder
public record DomainModelDTO(
        List<String> classes,
        List<RelationshipDto> relationships
) { }
