package com.devxmanish.DomainModelExtraction.models;

import com.devxmanish.DomainModelExtraction.enums.ExtractionPhase;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "intermediate_classes")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class IntermediateClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "story_id", nullable = false)
    private UserStory story;

    @ManyToOne
    @JoinColumn(name="job_id", nullable = false)
    private Job job;

    private String className;

    @Enumerated(EnumType.STRING)
    private ExtractionPhase extractionPhase;

    @LastModifiedDate
    private LocalDateTime timestamp;

    @OneToMany(mappedBy = "sourceClass", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<IntermediateRelationship> outgoingRelationships;

    @OneToMany(mappedBy = "targetClass", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<IntermediateRelationship> incomingRelationships;

    public IntermediateClass(Job job,UserStory story, String className, ExtractionPhase extractionPhase) {
        this.job=job;
        this.story = story;
        this.className= className;
        this.extractionPhase = extractionPhase;
    }
}
