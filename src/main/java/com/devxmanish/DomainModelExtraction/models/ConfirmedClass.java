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
import java.util.List;

@Entity
@Table(name = "confirmed_classes")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class ConfirmedClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String className;

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

    @OneToMany(mappedBy = "sourceClass", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private List<ConfirmedRelationship> outgoingRelationships;

    @OneToMany(mappedBy = "targetClass", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private List<ConfirmedRelationship> incomingRelationships;

}
