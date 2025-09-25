package com.devxmanish.DomainModelExtraction.dtos;

import java.util.List;


public record LLMResult(
        List<String> classes,
        List<LLMRelationshipDto> relationships
) { }
