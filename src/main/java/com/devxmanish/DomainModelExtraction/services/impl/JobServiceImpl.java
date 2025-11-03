package com.devxmanish.DomainModelExtraction.services.impl;

import com.devxmanish.DomainModelExtraction.dtos.JobDTO;
import com.devxmanish.DomainModelExtraction.dtos.JobEvent;
import com.devxmanish.DomainModelExtraction.dtos.Response;
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
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

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

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * Creates a Job and associated UserStory records from uploaded file
     */
    @Override
    public Response<?> createJob(MultipartFile file, String mode, String model) {
        log.info("Inside createJob()");

        User currentUser = userService.getCurrentLoggedInUser();

        Map<String, String> llmModels = Map.of(
                "ChatGPT", "openai/gpt-oss-120b:free",
                "Gemini","google/gemini-2.0-flash-exp:free",
                "Grok","x-ai/grok-code-fast-1",
                "DeepSeek","deepseek/deepseek-chat-v3.1:free",
                "Llama","meta-llama/llama-3.3-70b-instruct:free"
        );

        String selectedModel = llmModels.get(model);

        try {
            // Parse file immediately
            List<String> storiesText = parseFile(file);

            // Create Job
            Job job = new Job();
            job.setUserId(currentUser.getId());
            job.setStartTime(LocalDateTime.now());
            job.setJobType(mode.equalsIgnoreCase("STEP_BY_STEP") ? JobType.STEP_BY_STEP : JobType.BATCH);
            job.setStatus(Status.PENDING);
            job.setTotalStories(storiesText.size());
            job.setProcessedStories(0);
            job.setModel(selectedModel);
            Job finalJob = jobRepository.save(job);

            // Save UserStory entities
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

            // RETURN RESPONSE IMMEDIATELY for WebSocket connection
            Response<Map<String, Object>> response = Response.<Map<String, Object>>builder()
                    .statusCode(HttpStatus.OK.value())
                    .message("Job created successfully. Subscribe to /topic/jobs/" + job.getId() + " for updates.")
                    .data(Map.of("jobId", job.getId()))
                    .build();

            // Trigger async processing AFTER returning the response
            CompletableFuture.runAsync(() -> {
                try {
                // Small delay (1–2 seconds) to ensure frontend is subscribed
                Thread.sleep(2000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                processJobAsync(finalJob.getId(), userStories);

                }
            );

            return response;

        } catch (Exception e) {
            log.error("Error creating job: ", e);
            throw new RuntimeException("Failed to create job from uploaded file", e);
        }
    }


    @Async
    public void processJobAsync(Long jobId, List<UserStory> userStories) {
        log.info("Starting async processing for job: {}", jobId);

        try {
            Job job = jobRepository.findById(jobId)
                    .orElseThrow(() -> new RuntimeException("Job not found: " + jobId));

            // Processing logic based on job type
            if (job.getJobType() == JobType.STEP_BY_STEP) {
                for (UserStory story : userStories) {
                    messagingTemplate.convertAndSend("/topic/jobs/" + job.getId(),
                            new JobEvent(job.getId(), "PROCESSING_STORY", "story: " + story.getStoryText()));

                    llmProcessingService.processStory(job.getModel(), story);

                    messagingTemplate.convertAndSend("/topic/jobs/" + job.getId(),
                            new JobEvent(job.getId(), "STORY_PROCESSED", "story: " + story.getStoryText()));

                    job.setProcessedStories(job.getProcessedStories() + 1);
                    jobRepository.save(job);
                }
            } else {
                messagingTemplate.convertAndSend("/topic/jobs/" + job.getId(),
                        new JobEvent(job.getId(), "BATCH_PROCESSING_STARTED", "Processing all stories in batch."));

                llmProcessingService.processBatch(job.getModel(), userStories);

                messagingTemplate.convertAndSend("/topic/jobs/" + job.getId(),
                        new JobEvent(job.getId(), "BATCH_DONE", "Batch processing finished."));
            }

            // Finalize job
            job.setEndTime(LocalDateTime.now());
            job.setStatus(Status.PROCESSED);
            jobRepository.save(job);

            messagingTemplate.convertAndSend("/topic/jobs/" + job.getId(),
                    new JobEvent(job.getId(), "JOB_COMPLETED", "Job finished successfully."));

        } catch (Exception e) {
            log.error("Error processing job {}: ", jobId, e);

            // Update job status to failed
            jobRepository.findById(jobId).ifPresent(job -> {
                job.setStatus(Status.FAILED);
                job.setEndTime(LocalDateTime.now());
                jobRepository.save(job);
            });

            // Send failure event
            messagingTemplate.convertAndSend("/topic/jobs/" + jobId,
                    new JobEvent(jobId, "JOB_FAILED", "Job processing failed: " + e.getMessage()));
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

    @Override
    public Response<?> getAllJobs() {
        User user = userService.getCurrentLoggedInUser();
        List<JobDTO> jobs = jobRepository.findByUserId(user.getId())
                .stream()
                .map(j -> new JobDTO(j.getId(), j.getJobType(), j.getModel(), j.getTotalStories(),j.getProcessedStories(),j.getStatus(),j.getStartTime(),j.getEndTime()))
                .toList();
        return Response.<List<JobDTO>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Jobs fetched successfully")
                .data(jobs)
                .build();
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
