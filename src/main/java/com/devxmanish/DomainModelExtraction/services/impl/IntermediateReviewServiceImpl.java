package com.devxmanish.DomainModelExtraction.services.impl;

import com.devxmanish.DomainModelExtraction.dtos.ClassReviewDTO;
import com.devxmanish.DomainModelExtraction.dtos.RelationshipReviewDTO;
import com.devxmanish.DomainModelExtraction.dtos.StoryReviewDTO;
import com.devxmanish.DomainModelExtraction.enums.ExtractionPhase;
import com.devxmanish.DomainModelExtraction.enums.Status;
import com.devxmanish.DomainModelExtraction.exceptions.NotFoundException;
import com.devxmanish.DomainModelExtraction.models.IntermediateClass;
import com.devxmanish.DomainModelExtraction.models.IntermediateRelationship;
import com.devxmanish.DomainModelExtraction.models.Job;
import com.devxmanish.DomainModelExtraction.models.UserStory;
import com.devxmanish.DomainModelExtraction.repos.IntermediateClassRepository;
import com.devxmanish.DomainModelExtraction.repos.IntermediateRelationshipRepository;
import com.devxmanish.DomainModelExtraction.repos.JobRepository;
import com.devxmanish.DomainModelExtraction.repos.UserStoryRepository;
import com.devxmanish.DomainModelExtraction.services.IntermediateReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class IntermediateReviewServiceImpl implements IntermediateReviewService {

    @Autowired
    private IntermediateClassRepository classRepo;

    @Autowired
    private IntermediateRelationshipRepository relRepo;

    @Autowired
    private UserStoryRepository storyRepo;

    @Autowired
    private JobRepository jobRepo;


    @Override
    public List<IntermediateClass> getStoryIntermediateClasses(Long storyId) {
        return classRepo.findByStoryId(storyId);
    }

    @Override
    public List<IntermediateRelationship> getStoryIntermediateRelationships(Long storyId) {
        return relRepo.findByStoryId(storyId);
    }

    // for STEP_BY_STEP flow
    @Override
    public List<StoryReviewDTO> getIntermediateByStoryForJob(Long jobId) {
        List<StoryReviewDTO> stories = new ArrayList<>();

        // load all the user stories associated to a job
        List<UserStory> us = storyRepo.findByJobId(jobId);

        for (UserStory u : us) {
            List<ClassReviewDTO> ic = classRepo.findByStory(u).stream()
                    .map(c -> new ClassReviewDTO(c.getId(), c.getClassName()))
                    .toList();
            List<RelationshipReviewDTO> ir = relRepo.findByStory(u).stream()
                    .map(r -> new RelationshipReviewDTO(r.getId(), r.getSourceClass().getClassName(), r.getTargetClass().getClassName(), r.getRelationshipType()))
                    .toList();
            stories.add(StoryReviewDTO.builder()
                            .storyId(u.getId())
                            .storyText(u.getStoryText())
                            .status(u.getStatus())
                            .intermediateClasses(ic)
                            .intermediateRelationships(ir)
                    .build());
        }
        return stories;
    }

    @Override
    public List<IntermediateClass> getJobIntermediateClasses(Long jobId) {
        return classRepo.findByJobId(jobId);
    }

    @Override
    public List<IntermediateRelationship> getJobIntermediateRelationships(Long jobId) {
        return relRepo.findByJobId(jobId);
    }

    @Override
    public void addIntermediateClassForSBS(UserStory story, String className) {
        IntermediateClass newIC = IntermediateClass.builder()
                .className(className)
                .extractionPhase(ExtractionPhase.USER_ADDED)
                .timestamp(LocalDateTime.now())
                .story(story)
                .job(story.getJob())
                .build();
                classRepo.save(newIC);
    }

    @Override
    public void updateIntermediateClass(Long classId, String newName) {
        IntermediateClass ic = classRepo.findById(classId)
                .orElseThrow(()-> new NotFoundException("Class not found"));

        ic.setClassName(newName);
        ic.setExtractionPhase(ExtractionPhase.USER_EDITED);
        classRepo.save(ic);
    }

    @Override
    public void deleteIntermediateClass(Long classId) {
        IntermediateClass ic = classRepo.findById(classId)
                .orElseThrow(()-> new NotFoundException("Intermediate class not found"));
        ic.setExtractionPhase(ExtractionPhase.USER_DELETED);
        classRepo.save(ic);
    }

    @Override
    public void addIntermediateRelationshipSBS(UserStory story, Long srcId, Long tgtId, String type) {
        IntermediateClass sc = classRepo.findById(srcId)
                .orElseThrow(()-> new NotFoundException("Source Class not found"));

        IntermediateClass tc = classRepo.findById(tgtId)
                .orElseThrow(()-> new NotFoundException("Target Class not found"));

        IntermediateRelationship ir = IntermediateRelationship.builder()
                .story(story)
                .timestamp(LocalDateTime.now())
                .job(story.getJob())
                .sourceClass(sc)
                .targetClass(tc)
                .relationshipType(type)
                .build();
        relRepo.save(ir);
    }

    @Override
    public void updateIntermediateRelationship(Long relId, String newType) {
        IntermediateRelationship ir = relRepo.findById(relId)
                .orElseThrow(()-> new NotFoundException("Intermediate Relationship not found"));

        ir.setRelationshipType(newType);
        ir.setExtractionPhase(ExtractionPhase.USER_EDITED);
        relRepo.save(ir);
    }

    @Override
    public void deleteIntermediateRelationship(Long relId) {
        IntermediateRelationship ir = relRepo.findById(relId)
                        .orElseThrow(()-> new NotFoundException("Intermediate relationship not found"));

        ir.setExtractionPhase(ExtractionPhase.USER_DELETED);
        relRepo.save(ir);
    }

    @Override
    public void confirmStory(Long storyId) {
        UserStory story = storyRepo.findById(storyId)
                .orElseThrow(()-> new NotFoundException("Story not found"));
        story.setStatus(Status.CONFIRMED);
        storyRepo.save(story);
    }

    @Override
    public void confirmJob(Long jobId) {
        Job job = jobRepo.findById(jobId).orElseThrow();
        job.setStatus(Status.CONFIRMED);
        jobRepo.save(job);
    }
}
