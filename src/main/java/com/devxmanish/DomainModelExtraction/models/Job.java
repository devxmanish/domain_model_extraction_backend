package com.devxmanish.DomainModelExtraction.models;

import com.devxmanish.DomainModelExtraction.enums.JobType;
import com.devxmanish.DomainModelExtraction.enums.Status;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "jobs")
@Data
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private JobType jobType;

    private String model;

    private Long userId;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    private Status status;

    private Integer totalStories;
    private Integer processedStories;

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private List<UserStory> stories;

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private List<IntermediateClass> intermediateClasses;

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private List<IntermediateRelationship> intermediateRelationships;

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private List<ConfirmedClass> confirmedClasses;

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private List<ConfirmedRelationship> confirmedRelationships;

    // Getters & Setters
}
