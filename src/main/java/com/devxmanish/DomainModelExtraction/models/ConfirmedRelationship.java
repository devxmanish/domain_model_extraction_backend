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
@Table(name = "confirmed_relationships")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class ConfirmedRelationship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "source_class_id", nullable = false)
    @JsonBackReference
    private ConfirmedClass sourceClass;

    @ManyToOne
    @JoinColumn(name = "target_class_id", nullable = false)
    @JsonBackReference
    private ConfirmedClass targetClass;

    private String relationshipType;

    @Column(columnDefinition = "TEXT")
    private String storyIds; // JSON list

    @ManyToOne
    @JoinColumn(name = "job_id", nullable = false)
    @JsonBackReference
    private Job job;

    @Enumerated(EnumType.STRING)
    private ExtractionPhase extractionPhase;

    @LastModifiedDate
    private LocalDateTime timestamp;

    // Getters & Setters
}
