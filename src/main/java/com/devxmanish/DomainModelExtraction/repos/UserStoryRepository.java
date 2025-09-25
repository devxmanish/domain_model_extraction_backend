package com.devxmanish.DomainModelExtraction.repos;

import com.devxmanish.DomainModelExtraction.enums.Status;
import com.devxmanish.DomainModelExtraction.models.Job;
import com.devxmanish.DomainModelExtraction.models.UserStory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserStoryRepository extends JpaRepository<UserStory, Long> {
    List<UserStory> findByJob(Job job);
    List<UserStory> findByJobId(Long jobId);
    List<UserStory> findByStatus(Status status);
    List<UserStory> findByJobIdAndStatus(Long jobId, Status status);
}
