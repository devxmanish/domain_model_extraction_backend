package com.devxmanish.DomainModelExtraction.repos;

import com.devxmanish.DomainModelExtraction.enums.ExtractionPhase;
import com.devxmanish.DomainModelExtraction.models.ConfirmedClass;
import com.devxmanish.DomainModelExtraction.models.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ConfirmedClassRepository extends JpaRepository<ConfirmedClass, Long> {
    List<ConfirmedClass> findByJob(Job job);
    List<ConfirmedClass> findByJobId(Long jobId);
    List<ConfirmedClass> findByExtractionPhase(ExtractionPhase phase);
    ConfirmedClass findByClassNameAndJobId(String className, Long jobId); // for deduplication
}
