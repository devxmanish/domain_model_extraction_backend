package com.devxmanish.DomainModelExtraction.dtos;

import com.devxmanish.DomainModelExtraction.models.IntermediateClass;
import com.devxmanish.DomainModelExtraction.models.IntermediateRelationship;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeduplicationLLMPayload {

    private List<ClassPayload> classes;
    private List<RelationshipPayload> relationships;

    // === Inner DTOs ===
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ClassPayload {
        private String name;
        private Long storyId;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RelationshipPayload {
        private String source;
        private String target;
        private String type;
        private Long storyId;
    }

    // === Factory Method ===
    public static DeduplicationLLMPayload from(List<IntermediateClass> classes,
                                               List<IntermediateRelationship> relationships) {
        DeduplicationLLMPayload payload = new DeduplicationLLMPayload();

        payload.classes = classes.stream()
                .map(ic -> new ClassPayload(
                        ic.getClassName(),
                        ic.getStory() != null ? ic.getStory().getId() : null
                ))
                .collect(Collectors.toList());

        payload.relationships = relationships.stream()
                .map(ir -> new RelationshipPayload(
                        ir.getSourceClass().getClassName(),
                        ir.getTargetClass().getClassName(),
                        ir.getRelationshipType(),
                        ir.getStory() != null ? ir.getStory().getId() : null
                ))
                .collect(Collectors.toList());

        return payload;
    }
}
