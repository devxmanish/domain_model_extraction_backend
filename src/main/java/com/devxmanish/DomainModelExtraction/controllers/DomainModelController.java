package com.devxmanish.DomainModelExtraction.controllers;

import com.devxmanish.DomainModelExtraction.dtos.DomainModelDTO;
import com.devxmanish.DomainModelExtraction.dtos.Response;
import com.devxmanish.DomainModelExtraction.dtos.ReviewDTO;
import com.devxmanish.DomainModelExtraction.services.DomainModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/domain-model")
public class DomainModelController {

    @Autowired
    private DomainModelService domainModelService;

    // === Review Phase ===

    @GetMapping("/review/{jobId}")
    public ResponseEntity<Response<ReviewDTO>> getReviewData(@PathVariable Long jobId) {
        return ResponseEntity.ok(domainModelService.getReviewData(jobId));
    }

    // --- STEP_BY_STEP specific ---
    @PostMapping("/review/story/{storyId}/confirm")
    public ResponseEntity<Response<?>> confirmStory(@PathVariable Long storyId) {
        return ResponseEntity.ok(domainModelService.confirmStory(storyId));

    }

    // --- BATCH specific ---
    @PostMapping("/review/job/{jobId}/confirm")
    public void confirmJob(@PathVariable Long jobId) {
        domainModelService.confirmJob(jobId);
    }

    // --- CRUD on Intermediate Classes ---

    // this is only valid for the STEP_BY_STEP process review
    @PostMapping("/review/story/{storyId}/class")
    public ResponseEntity<Response<?>> addIntermediateClass(@PathVariable Long storyId,
                                                  @RequestParam String className) {
        return ResponseEntity.ok(domainModelService.addIntermediateClassForSBS(storyId, className));
    }

    // this is only valid for the STEP_BY_STEP process review
    @PostMapping("/review/story/{jobId}/class/batch")
    public ResponseEntity<Response<?>> addIntermediateClassBM(@PathVariable Long jobId,
                                                            @RequestParam String className) {
        return ResponseEntity.ok(domainModelService.addIntermediateClassForBM(jobId, className));
    }

    // used by both Batch and Step_By_Step
    @PutMapping("/review/class/{classId}")
    public ResponseEntity<Response<?>> updateIntermediateClass(@PathVariable Long classId,
                                        @RequestParam String newName) {
        return ResponseEntity.ok(domainModelService.updateIntermediateClass(classId, newName));
    }

    // used by both Batch and Step_By_Step
    @DeleteMapping("/review/class/{classId}")
    public ResponseEntity<Response<?>> deleteIntermediateClass(@PathVariable Long classId) {
        return ResponseEntity.ok(domainModelService.deleteIntermediateClass(classId));
    }

    // --- CRUD on Intermediate Relationships ---

    // only for the Step_By_Step
    @PostMapping("/review/story/{storyId}/relationship")
    public ResponseEntity<Response<?>> addIntermediateRelationship(@PathVariable Long storyId,
                                                                @RequestParam Long srcId,
                                                                @RequestParam Long tgtId,
                                                                @RequestParam String type) {
        return ResponseEntity.ok(domainModelService.addIntermediateRelationship(storyId, srcId, tgtId, type));
    }

    @PostMapping("/review/story/{jobId}/relationship/batch")
    public ResponseEntity<Response<?>> addIntermediateRelationshipBM(@PathVariable Long jobId,
                                                                   @RequestParam Long srcId,
                                                                   @RequestParam Long tgtId,
                                                                   @RequestParam String type) {
        return ResponseEntity.ok(domainModelService.addIntermediateRelationshipBM(jobId, srcId, tgtId, type));
    }

    @PutMapping("/review/relationship/{relId}")
    public ResponseEntity<Response<?>> updateIntermediateRelationship(@PathVariable Long relId,
                                               @RequestParam String newType) {
        return ResponseEntity.ok(domainModelService.updateIntermediateRelationship(relId, newType));
    }

    @DeleteMapping("/review/relationship/{relId}")
    public ResponseEntity<Response<?>> deleteIntermediateRelationship(@PathVariable Long relId) {
        return ResponseEntity.ok(domainModelService.deleteIntermediateRelationship(relId));
    }

    // === Deduplication / Transfer ===

    @PostMapping("/review/job/{jobId}/deduplicate")
    public ResponseEntity<Response<?>> deduplicateAndConsolidate(@PathVariable Long jobId) {
        return ResponseEntity.ok(domainModelService.deduplicateAndConsolidate(jobId));
    }

//    @PostMapping("/review/job/{jobId}/transfer")
//    public void transferToConfirmed(@PathVariable Long jobId) {
//        domainModelService.transferToConfirmed(jobId);
//    }


    // === Consolidated Review Phase ===
    @GetMapping("/review/job/{jobId}/consolidated")
    public ResponseEntity<Response<?>> getConsolidatedReview(@PathVariable Long jobId) {
        return ResponseEntity.ok(domainModelService.getConsolidatedReview(jobId));
    }

    @PostMapping("/confirmed/{jobId}/class")
    public ResponseEntity<Response<?>> addConfirmedClass(@PathVariable Long jobId,
                                            @RequestParam String className) {
        return ResponseEntity.ok(domainModelService.addConfirmedClass(jobId, className));
    }

    @PutMapping("/confirmed/class/{classId}")
    public ResponseEntity<Response<?>> updateConfirmedClass(@PathVariable Long classId,
                                     @RequestParam String newName) {
        return ResponseEntity.ok(domainModelService.updateConfirmedClass(classId, newName));
    }

    @DeleteMapping("/confirmed/class/{classId}")
    public ResponseEntity<Response<?>> deleteConfirmedClass(@PathVariable Long classId) {
        return ResponseEntity.ok(domainModelService.deleteConfirmedClass(classId));
    }

    @PostMapping("/confirmed/{jobId}/relationship")
    public ResponseEntity<Response<?>> addConfirmedRelationship(@PathVariable Long jobId,
                                                          @RequestParam Long srcId,
                                                          @RequestParam Long tgtId,
                                                          @RequestParam String type) {
        return ResponseEntity.ok(domainModelService.addConfirmedRelationship(jobId, srcId, tgtId, type));
    }

    @PutMapping("/confirmed/relationship/{relId}")
    public ResponseEntity<Response<?>> updateConfirmedRelationship(@PathVariable Long relId,
                                            @RequestParam String newType) {
        return ResponseEntity.ok(domainModelService.updateConfirmedRelationship(relId, newType));
    }

    @DeleteMapping("/confirmed/relationship/{relId}")
    public ResponseEntity<Response<?>> deleteConfirmedRelationship(@PathVariable Long relId) {
        return ResponseEntity.ok(domainModelService.deleteConfirmedRelationship(relId));
    }

    //     === Final Domain Model ===

    @GetMapping("/final/{jobId}")
    public ResponseEntity<Response<?>> getFinalDomainModel(@PathVariable Long jobId) {
        return ResponseEntity.ok(domainModelService.getFinalDomainModel(jobId));
    }
}
