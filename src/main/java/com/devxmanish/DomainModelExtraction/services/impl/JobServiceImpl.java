package com.devxmanish.DomainModelExtraction.services.impl;

import com.devxmanish.DomainModelExtraction.enums.JobType;
import com.devxmanish.DomainModelExtraction.enums.Status;
import com.devxmanish.DomainModelExtraction.models.Job;
import com.devxmanish.DomainModelExtraction.models.User;
import com.devxmanish.DomainModelExtraction.models.UserStory;
import com.devxmanish.DomainModelExtraction.repos.JobRepository;
import com.devxmanish.DomainModelExtraction.repos.UserStoryRepository;
import com.devxmanish.DomainModelExtraction.services.JobService;
import com.devxmanish.DomainModelExtraction.services.LLMProcessingService;
import com.devxmanish.DomainModelExtraction.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class JobServiceImpl implements JobService {

    @Autowired
    private UserService userService;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private UserStoryRepository userStoryRepository;

    @Autowired
    private LLMProcessingService llmProcessingService;

    /**
     * Creates a Job and associated UserStory records from uploaded file
     */
    @Override
    public Job createJob(MultipartFile file, String mode, String model) {
        log.info("Inside createJob()");

        User currentUser = userService.getCurrentLoggedInUser();
        try {
            // Parse file into individual stories
            List<String> storiesText = parseFile(file);

            // Create Job
            Job job = new Job();
            job.setUserId(currentUser.getId());
            job.setStartTime(LocalDateTime.now());
            job.setJobType(mode.equalsIgnoreCase("STEP_BY_STEP") ? JobType.STEP_BY_STEP : JobType.BATCH);
            job.setStatus(Status.PENDING);
            job.setTotalStories(storiesText.size());
            job.setProcessedStories(0);
            job.setModel(model); // optional model field in Job

            job = jobRepository.save(job);

            // Create UserStory entities
            List<UserStory> userStories = new ArrayList<>();
            for (String storyText : storiesText) {
                UserStory story = new UserStory();
                story.setJob(job);
                story.setStoryText(storyText);
                story.setUploadTime(LocalDateTime.now());
                story.setStatus(Status.PENDING);
                story.setUserId(currentUser.getId());
                userStories.add(story);
            }
            userStoryRepository.saveAll(userStories);

            // Trigger LLM processing
            if (job.getJobType() == JobType.STEP_BY_STEP) {
                for (UserStory story : userStories) {
                    llmProcessingService.processStory(job,story);
                }
            } else {
                llmProcessingService.processBatch(userStories);
            }

            return job;

        } catch (Exception e) {
            throw new RuntimeException("Failed to create job from uploaded file", e);
        }
    }

    /**
     * Returns all stories associated with a job
     */
    @Override
    public List<UserStory> getStoriesByJob(Long jobId) {
        log.info("Inside getStoriesByJob()");

        return userStoryRepository.findByJobId(jobId);
    }

    /**
     * Returns job details
     */
    @Override
    public Job getJob(Long jobId) {
        log.info("Inside getJob()");

        return jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found with id: " + jobId));
    }

    /**
     * Helper method to parse the uploaded file into list of story strings
     */
    private List<String> parseFile(MultipartFile file) throws Exception {
        log.info("Inside parseFile()");

        List<String> stories = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    stories.add(line);
                }
            }
        }
        return stories;
    }
}
