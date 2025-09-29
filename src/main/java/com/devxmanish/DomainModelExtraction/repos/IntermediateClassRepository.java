package com.devxmanish.DomainModelExtraction.repos;

import com.devxmanish.DomainModelExtraction.enums.ExtractionPhase;
import com.devxmanish.DomainModelExtraction.models.IntermediateClass;
import com.devxmanish.DomainModelExtraction.models.UserStory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface IntermediateClassRepository extends JpaRepository<IntermediateClass, Long> {
    List<IntermediateClass> findByStory(UserStory story);
    List<IntermediateClass> findByStoryId(Long storyId);
    List<IntermediateClass> findByJobId(Long storyId);
    List<IntermediateClass> findByStoryIdAndExtractionPhase(Long storyId, ExtractionPhase phase);
    List<IntermediateClass> findByClassNameAndStoryId(String className, Long storyId);
}
