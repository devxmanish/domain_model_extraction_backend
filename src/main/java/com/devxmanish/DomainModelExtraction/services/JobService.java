package com.devxmanish.DomainModelExtraction.services;

import com.devxmanish.DomainModelExtraction.models.Job;
import com.devxmanish.DomainModelExtraction.models.UserStory;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface JobService {
    Job createJob(MultipartFile file, String mode, String model);
    List<UserStory> getStoriesByJob(Long jobId);
    Job getJob(Long jobId);
}
