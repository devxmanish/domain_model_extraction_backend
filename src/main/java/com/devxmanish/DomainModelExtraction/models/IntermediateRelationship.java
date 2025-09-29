package com.devxmanish.DomainModelExtraction.models;

import com.devxmanish.DomainModelExtraction.enums.ExtractionPhase;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "intermediate_relationships")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class IntermediateRelationship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "story_id", nullable = false)
    @JsonBackReference
    private UserStory story;

    @ManyToOne
    @JoinColumn(name="job_id", nullable = false)
    @JsonBackReference
    private Job job;

    @ManyToOne
    @JoinColumn(name = "source_class_id", nullable = false)
    @JsonBackReference
    private IntermediateClass sourceClass;

    @ManyToOne
    @JoinColumn(name = "target_class_id", nullable = false)
    @JsonBackReference
    private IntermediateClass targetClass;

    private String relationshipType;

    @Enumerated(EnumType.STRING)
    private ExtractionPhase extractionPhase;

    @LastModifiedDate
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
