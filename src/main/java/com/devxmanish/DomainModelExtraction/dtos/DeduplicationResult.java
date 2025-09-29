package com.devxmanish.DomainModelExtraction.dtos;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeduplicationResult {

    private List<ClassResult> classes;
    private List<RelationshipResult> relationships;

    // === Inner DTOs ===
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ClassResult {
        private String name;
        private List<Long> storyIds;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RelationshipResult {
        private String source;
        private String target;
        private String type;
        private List<Long> storyIds;
    }

    // === Utility ===
    public static DeduplicationResult fromJson(String json) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(json, DeduplicationResult.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse deduplication result JSON", e);
        }
    }

}
