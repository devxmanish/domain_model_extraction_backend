package com.devxmanish.DomainModelExtraction.dtos;

import com.devxmanish.DomainModelExtraction.models.ConfirmedClass;
import com.devxmanish.DomainModelExtraction.models.ConfirmedRelationship;
import com.devxmanish.DomainModelExtraction.models.UserStory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class ConsolidatedReviewDTO {

    private Long jobId;
    private String jobType;

    private List<ClassDTO> classes;
    private List<RelationshipDTO> relationships;

    // === Inner DTOs ===

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class StoryDTO {
        private Long id;
        private String text;

        public static StoryDTO from(UserStory story) {
            return new StoryDTO(story.getId(), story.getStoryText());
        }
    }

    @Getter
    public static class ClassDTO {
        private Long id;
        private String className;
        private List<StoryDTO> stories;

        public static ClassDTO from(ConfirmedClass cc, List<UserStory> associatedStories) {
            ClassDTO dto = new ClassDTO();
            dto.id = cc.getId();
            dto.className = cc.getClassName();
            dto.stories = associatedStories.stream()
                    .map(StoryDTO::from)
                    .collect(Collectors.toList());
            return dto;
        }
    }

    @Getter
    public static class RelationshipDTO {
        private Long id;
        private ClassSummaryDTO source;
        private ClassSummaryDTO target;
        private String type;
        private List<StoryDTO> stories;

        public static RelationshipDTO from(ConfirmedRelationship rel,
                                           ConfirmedClass src,
                                           ConfirmedClass tgt,
                                           List<UserStory> associatedStories) {
            RelationshipDTO dto = new RelationshipDTO();
            dto.id = rel.getId();
            dto.type = rel.getRelationshipType();
            dto.source = new ClassSummaryDTO(src.getId(), src.getClassName());
            dto.target = new ClassSummaryDTO(tgt.getId(), tgt.getClassName());
            dto.stories = associatedStories.stream()
                    .map(StoryDTO::from)
                    .collect(Collectors.toList());
            return dto;
        }

    }

    @Getter
    @AllArgsConstructor
    public static class ClassSummaryDTO {
        private Long id;
        private String name;
    }

    // === Factory ===
    public static ConsolidatedReviewDTO from(Long jobId,
                                             String jobType,
                                             List<ClassDTO> classes,
                                             List<RelationshipDTO> relationships) {
        ConsolidatedReviewDTO dto = new ConsolidatedReviewDTO();
        dto.jobId = jobId;
        dto.jobType = jobType;
        dto.classes = classes;
        dto.relationships = relationships;
        return dto;
    }

}
