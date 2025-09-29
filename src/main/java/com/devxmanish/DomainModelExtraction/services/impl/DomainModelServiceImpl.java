package com.devxmanish.DomainModelExtraction.services.impl;

import com.devxmanish.DomainModelExtraction.dtos.*;
import com.devxmanish.DomainModelExtraction.enums.JobType;
import com.devxmanish.DomainModelExtraction.exceptions.NotFoundException;
import com.devxmanish.DomainModelExtraction.models.*;
import com.devxmanish.DomainModelExtraction.repos.JobRepository;
import com.devxmanish.DomainModelExtraction.repos.UserStoryRepository;
import com.devxmanish.DomainModelExtraction.services.ConfirmedReviewService;
import com.devxmanish.DomainModelExtraction.services.DeduplicationService;
import com.devxmanish.DomainModelExtraction.services.DomainModelService;
import com.devxmanish.DomainModelExtraction.services.IntermediateReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DomainModelServiceImpl implements DomainModelService {

    @Autowired
    private JobRepository jobRepo;

    @Autowired
    private UserStoryRepository storyRepository;

    @Autowired
    private IntermediateReviewService intermediateReviewService;

    @Autowired
    private DeduplicationService deduplicationService;

    @Autowired
    private ConfirmedReviewService confirmedReviewService;

    @Override
    public Response<ReviewDTO> getReviewData(Long jobId) {
        Job job = jobRepo.findById(jobId)
                .orElseThrow(()-> new NotFoundException("Job Not Found"));

        if (job.getJobType() == JobType.BATCH) {
            return Response.<ReviewDTO>builder()
                    .statusCode(HttpStatus.OK.value())
                    .message("Intermediate Review data fetched")
                    .data(ReviewDTO.fromBatch(job,
                            intermediateReviewService.getJobIntermediateClasses(jobId)
                                    .stream()
                                    .map(c -> new ClassReviewDTO(c.getId(),c.getClassName()))
                                    .toList(),
                            intermediateReviewService.getJobIntermediateRelationships(jobId)
                                    .stream()
                                    .map(r -> new RelationshipReviewDTO(r.getId(), r.getSourceClass().getClassName(), r.getTargetClass().getClassName(), r.getRelationshipType()))
                                    .toList()
                    ))
                    .build();
        } else {
            return Response.<ReviewDTO>builder()
                    .statusCode(HttpStatus.OK.value())
                    .message("Intermediate review data fetched")
                    .data(ReviewDTO.fromStepByStep(job, intermediateReviewService.getIntermediateByStoryForJob(jobId)))
                    .build();
        }
    }

    @Override
    public Response<?> confirmStory(Long storyId) {
        intermediateReviewService.confirmStory(storyId);
        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Story confirmed successfully, move to next story...")
                .build();
    }

    @Override
    public void confirmJob(Long jobId) {
        intermediateReviewService.confirmJob(jobId);
    }

    @Override
    public Response<?> deduplicateAndConsolidate(Long jobId) {
         deduplicationService.deduplicateAndConsolidate(jobId);
        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Deduplication and Consolidation completed successfully")
                .build();
    }

    @Override
    public void transferToConfirmed(Long jobId) {
        deduplicationService.transferToConfirmed(jobId);
    }

    @Override
    public Response<?> getConsolidatedReview(Long jobId) {
        ConsolidatedReviewDTO dto = confirmedReviewService.getConsolidatedReview(jobId);
        return Response.<ConsolidatedReviewDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message("consolidated review data fetched")
                .data(dto)
                .build();
    }

    @Override
    public List<ConfirmedClass> getConfirmedClasses(Long jobId) {
        return confirmedReviewService.getConfirmedClasses(jobId);
    }

    @Override
    public List<ConfirmedRelationship> getConfirmedRelationships(Long jobId) {
        return confirmedReviewService.getConfirmedRelationships(jobId);
    }

    @Override
    public Response<?> addIntermediateClassForSBS(Long storyId, String className) {
        //check the story is valid
        UserStory story = storyRepository.findById(storyId)
                .orElseThrow(()-> new NotFoundException("Story not found"));

        //check either this story is associated with BATCH or STEP_BY_STEP job
        if(story.getJob().getJobType() == JobType.STEP_BY_STEP){
            intermediateReviewService.addIntermediateClassForSBS(story,className);
        }
        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Intermediate classes added successfully")
                .build();
    }

    @Override
    public Response<?> updateIntermediateClass(Long classId, String className) {
        intermediateReviewService.updateIntermediateClass(classId, className);
        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Intermediate class updated successfully")
                .build();
    }

    @Override
    public Response<?> deleteIntermediateClass(Long classId) {
        intermediateReviewService.deleteIntermediateClass(classId);
        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Intermediate Class Deleted")
                .build();
    }

    @Override
    public Response<?> addIntermediateRelationship(Long storyId, Long srcId, Long tgtId, String type) {
        UserStory story = storyRepository.findById(storyId)
                .orElseThrow(()-> new NotFoundException("Story not found"));

        if(story.getJob().getJobType() == JobType.STEP_BY_STEP){
            intermediateReviewService.addIntermediateRelationshipSBS(story, srcId, tgtId, type);
        }

        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Intermediate relationship added successfully")
                .build();
    }

    @Override
    public Response<?> updateIntermediateRelationship(Long relId, String newType) {
        intermediateReviewService.updateIntermediateRelationship(relId,newType);
        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Intermediate Relationship updated successfully")
                .build();
    }

    @Override
    public Response<?> deleteIntermediateRelationship(Long relId) {
        intermediateReviewService.deleteIntermediateRelationship(relId);
        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Intermediate Relationship Deleted successfully")
                .build();
    }

    @Override
    public Response<?> addConfirmedClass(Long jobId, String className) {
        confirmedReviewService.addConfirmedClass(jobId, className);
        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Confirmed Classes Added Successfully")
                .build();
    }

    @Override
    public Response<?> updateConfirmedClass(Long classId, String newName) {
        confirmedReviewService.updateConfirmedClass(classId, newName);
        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Confirmed classes Update Successfully")
                .build();
    }

    @Override
    public Response<?> deleteConfirmedClass(Long classId) {
        confirmedReviewService.deleteConfirmedClass(classId);
        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Confirmed Class deleted successfully")
                .build();
    }

    @Override
    public Response<?> addConfirmedRelationship(Long jobId, Long srcId, Long tgtId, String type) {
        confirmedReviewService.addConfirmedRelationship(jobId, srcId, tgtId, type);
        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Confirmed relationship added successfully")
                .build();
    }

    @Override
    public Response<?> updateConfirmedRelationship(Long relId, String newType) {
        confirmedReviewService.updateConfirmedRelationship(relId, newType);
        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("confirmed relationship updated successfully")
                .build();
    }

    @Override
    public Response<?> deleteConfirmedRelationship(Long relId) {
        confirmedReviewService.deleteConfirmedRelationship(relId);
        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Confirmed relationship deleted successfully")
                .build();
    }

    @Override
    public Response<?> getFinalDomainModel(Long jobId) {
        return Response.<DomainModelDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Final Domain Model fetched")
                .data(confirmedReviewService.getFinalDomainModel(jobId))
                .build();
    }

}
