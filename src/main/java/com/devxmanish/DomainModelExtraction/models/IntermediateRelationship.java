package com.devxmanish.DomainModelExtraction.models;

import com.devxmanish.DomainModelExtraction.enums.ExtractionPhase;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "intermediate_relationships")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IntermediateRelationship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "story_id", nullable = false)
    private UserStory story;

    @ManyToOne
    @JoinColumn(name="job_id", nullable = false)
    private Job job;

    @ManyToOne
    @JoinColumn(name = "source_class_id", nullable = false)
    private IntermediateClass sourceClass;

    @ManyToOne
    @JoinColumn(name = "target_class_id", nullable = false)
    private IntermediateClass targetClass;

    private String relationshipType;

    @Enumerated(EnumType.STRING)
    private ExtractionPhase extractionPhase;

    private LocalDateTime timestamp;

    public IntermediateRelationship(Job job,UserStory story, IntermediateClass sourceClass, IntermediateClass targetClass, String type, ExtractionPhase extractionPhase) {
        this.job=job;
        this.story = story;
        this.sourceClass=sourceClass;
        this.targetClass=targetClass;
        this.relationshipType=type;
        this.extractionPhase=extractionPhase;
    }

    // Getters & Setters
}
