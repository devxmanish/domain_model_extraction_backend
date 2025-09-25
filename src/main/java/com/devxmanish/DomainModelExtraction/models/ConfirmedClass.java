package com.devxmanish.DomainModelExtraction.models;

import com.devxmanish.DomainModelExtraction.enums.ExtractionPhase;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "confirmed_classes")
public class ConfirmedClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String className;

    @Column(columnDefinition = "TEXT")
    private String storyIds; // JSON list

    @ManyToOne
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @Enumerated(EnumType.STRING)
    private ExtractionPhase extractionPhase;

    private LocalDateTime timestamp;

    @OneToMany(mappedBy = "sourceClass", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ConfirmedRelationship> outgoingRelationships;

    @OneToMany(mappedBy = "targetClass", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ConfirmedRelationship> incomingRelationships;

    // Getters & Setters
}
