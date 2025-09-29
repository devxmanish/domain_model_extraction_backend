package com.devxmanish.DomainModelExtraction.models;

import com.devxmanish.DomainModelExtraction.enums.Status;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "user_stories")
@Data
public class UserStory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "job_id", nullable = false)
    @JsonBackReference
    private Job job;

    @Column(columnDefinition = "TEXT")
    private String storyText;

    private LocalDateTime uploadTime;

    @Enumerated(EnumType.STRING)
    private Status status;

    private Long userId;

    @OneToMany(mappedBy = "story", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private List<IntermediateClass> intermediateClasses;

    @OneToMany(mappedBy = "story", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private List<IntermediateRelationship> intermediateRelationships;

    // Getters & Setters
}
