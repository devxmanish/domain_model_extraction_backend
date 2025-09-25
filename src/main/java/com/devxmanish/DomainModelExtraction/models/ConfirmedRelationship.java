package com.devxmanish.DomainModelExtraction.models;

import com.devxmanish.DomainModelExtraction.enums.ExtractionPhase;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "confirmed_relationships")
public class ConfirmedRelationship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "source_class_id", nullable = false)
    private ConfirmedClass sourceClass;

    @ManyToOne
    @JoinColumn(name = "target_class_id", nullable = false)
    private ConfirmedClass targetClass;

    private String relationshipType;

    @Column(columnDefinition = "TEXT")
    private String storyIds; // JSON list

    @ManyToOne
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @Enumerated(EnumType.STRING)
    private ExtractionPhase extractionPhase;

    private LocalDateTime timestamp;

    // Getters & Setters
}
