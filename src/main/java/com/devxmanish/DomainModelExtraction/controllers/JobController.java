package com.devxmanish.DomainModelExtraction.controllers;

import com.devxmanish.DomainModelExtraction.dtos.Response;
import com.devxmanish.DomainModelExtraction.models.Job;
import com.devxmanish.DomainModelExtraction.services.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    @Autowired
    private JobService jobService;

    /**
     * Upload user stories and create a job.
     * JobService internally triggers LLM processing.
     */
    @PostMapping("/upload")
    public ResponseEntity<Response<?>> uploadJob(
            @RequestParam("file") MultipartFile file,
            @RequestParam("mode") String mode,          // STEP_BY_STEP or BATCH
            @RequestParam(value = "model", required = false) String model
    ) {
        // jobService.createJob now returns a Response with jobId immediately,
        // while processing + step updates are pushed over WebSocket.
        Response<?> response = jobService.createJob(file, mode, model);

        return ResponseEntity.ok(response);
    }


    @GetMapping()
    public ResponseEntity<Response<?>> getAllJobs(){
        return ResponseEntity.ok(jobService.getAllJobs());
    }
}
