package com.devxmanish.DomainModelExtraction.services.impl;

import com.devxmanish.DomainModelExtraction.dtos.ConsolidatedReviewDTO;
import com.devxmanish.DomainModelExtraction.dtos.DomainModelDTO;
import com.devxmanish.DomainModelExtraction.dtos.RelationshipDto;
import com.devxmanish.DomainModelExtraction.enums.ExtractionPhase;
import com.devxmanish.DomainModelExtraction.exceptions.NotFoundException;
import com.devxmanish.DomainModelExtraction.models.ConfirmedClass;
import com.devxmanish.DomainModelExtraction.models.ConfirmedRelationship;
import com.devxmanish.DomainModelExtraction.models.Job;
import com.devxmanish.DomainModelExtraction.models.UserStory;
import com.devxmanish.DomainModelExtraction.repos.ConfirmedClassRepository;
import com.devxmanish.DomainModelExtraction.repos.ConfirmedRelationshipRepository;
import com.devxmanish.DomainModelExtraction.repos.JobRepository;
import com.devxmanish.DomainModelExtraction.repos.UserStoryRepository;
import com.devxmanish.DomainModelExtraction.services.ConfirmedReviewService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class ConfirmedReviewServiceImpl implements ConfirmedReviewService {

    @Autowired
    private ConfirmedClassRepository classRepo;

    @Autowired
    private ConfirmedRelationshipRepository relRepo;

    @Autowired
    private UserStoryRepository storyRepo;

    @Autowired
    private JobRepository jobRepository;

    @Override
    public List<ConfirmedClass> getConfirmedClasses(Long jobId) {
        log.info("Inside getConfirmedCLasses()");

        return classRepo.findByJobId(jobId);
    }

    @Override
    public List<ConfirmedRelationship> getConfirmedRelationships(Long jobId) {
        log.info("getConfirmedRelationships()");

        return relRepo.findByJobId(jobId);
    }

    @Override
    public ConsolidatedReviewDTO getConsolidatedReview(Long jobId) {
        log.info("Inside getConsolidateReview()");

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new NotFoundException("Job Not Found"));

        List<ConfirmedClass> confirmedClasses = classRepo.findByJobId(jobId);
        List<ConfirmedRelationship> confirmedRels = relRepo.findByJobId(jobId);

        // build map of storyId -> UserStory
        Map<Long, UserStory> storyMap = storyRepo.findByJobId(jobId).stream()
                .collect(Collectors.toMap(UserStory::getId, s -> s));

        // map classes
        List<ConsolidatedReviewDTO.ClassDTO> classDTOs = confirmedClasses.stream()
                .map(cc -> {
                    List<Long> storyIds = parseStoryIds(cc.getStoryIds());
                    List<UserStory> stories = storyIds.stream()
                            .map(storyMap::get)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList());
                    return ConsolidatedReviewDTO.ClassDTO.from(cc, stories);
                })
                .collect(Collectors.toList());

        // map relationships
        List<ConsolidatedReviewDTO.RelationshipDTO> relDTOs = confirmedRels.stream()
                .map(rel -> {
                    ConfirmedClass src = confirmedClasses.stream()
                            .filter(c -> c.getId().equals(rel.getSourceClass().getId()))
                            .findFirst().orElseThrow();
                    ConfirmedClass tgt = confirmedClasses.stream()
                            .filter(c -> c.getId().equals(rel.getTargetClass().getId()))
                            .findFirst().orElseThrow();
                    List<Long> storyIds = parseStoryIds(rel.getStoryIds());
                    List<UserStory> relStories = storyIds.stream()
                            .map(storyMap::get)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList());
                    return ConsolidatedReviewDTO.RelationshipDTO.from(rel, src, tgt, relStories);
                })
                .collect(Collectors.toList());

        // return DTO
        return ConsolidatedReviewDTO.from(
                jobId,
                job.getJobType().name(),
                classDTOs,
                relDTOs
        );
    }

    @Override
    public void addConfirmedClass(Long jobId, String className) {
        log.info("Inside addConfirmedClass()");

        Job job = jobRepository.findById(jobId)
                .orElseThrow(()-> new NotFoundException("Job not found"));

        ConfirmedClass cc = ConfirmedClass.builder()
                .job(job)
                .className(className)
                .extractionPhase(ExtractionPhase.USER_ADDED)
                .storyIds("[]")
                .build();
        classRepo.save(cc);
    }

    @Override
    public void updateConfirmedClass(Long classId, String newName) {
        log.info("Inside updateConfirmedClass()");

        ConfirmedClass cc = classRepo.findById(classId)
                .orElseThrow(()-> new NotFoundException("Confirmed Class not found"));

        cc.setClassName(newName);
        cc.setExtractionPhase(ExtractionPhase.USER_EDITED);
        classRepo.save(cc);
    }

    @Override
    public void deleteConfirmedClass(Long classId) {
        log.info("Inside deleteConfirmedClass()");

        ConfirmedClass cc = classRepo.findById(classId)
                .orElseThrow(()-> new NotFoundException("Confirmed Class not found"));

        cc.setExtractionPhase(ExtractionPhase.USER_DELETED);
        classRepo.save(cc);
    }

    @Override
    public void addConfirmedRelationship(Long jobId, Long srcId, Long tgtId, String type) {
        log.info("Inside addConfirmedRelationship()");

        Job job = jobRepository.findById(jobId)
                .orElseThrow(()-> new NotFoundException("Job not found"));

        ConfirmedClass scc = classRepo.findById(srcId)
                .orElseThrow(()-> new NotFoundException("Source class not found"));

        ConfirmedClass tcc = classRepo.findById(srcId)
                .orElseThrow(()-> new NotFoundException("Source class not found"));

        ConfirmedRelationship cr = ConfirmedRelationship.builder()
                .job(job)
                .extractionPhase(ExtractionPhase.USER_ADDED)
                .sourceClass(scc)
                .targetClass(tcc)
                .relationshipType(type)
                .storyIds("[]")
                .build();
        relRepo.save(cr);
    }

    @Override
    public void updateConfirmedRelationship(Long relId, String newType) {
        log.info("Inside updateConfirmedRelationship()");

        ConfirmedRelationship cr = relRepo.findById(relId)
                .orElseThrow(()-> new NotFoundException("Confirmed Relationship not found"));
        cr.setRelationshipType(newType);
        cr.setExtractionPhase(ExtractionPhase.USER_EDITED);
        relRepo.save(cr);
    }

    @Override
    public void deleteConfirmedRelationship(Long relId) {
        log.info("Inside deleteConfirmedRelationship()");

        ConfirmedRelationship cr = relRepo.findById(relId)
                .orElseThrow(()-> new NotFoundException("Confirmed Relationship not found"));

        cr.setExtractionPhase(ExtractionPhase.USER_DELETED);
        relRepo.save(cr);
    }

    @Override
    public DomainModelDTO getFinalDomainModel(Long jobId) {
        log.info("Inside getFinalDomainModel()");

        Job job = jobRepository.findById(jobId)
                .orElseThrow(()-> new NotFoundException("Job not found"));

        List<String> cc = classRepo.findByJobId(jobId).stream()
                .filter(c -> c.getExtractionPhase() != ExtractionPhase.USER_DELETED)
                .map(ConfirmedClass::getClassName)
                .toList();

        List<RelationshipDto> cr = relRepo.findByJobId(jobId).stream()
                .filter(r -> r.getExtractionPhase() != ExtractionPhase.USER_DELETED)
                .map(r -> new RelationshipDto(
                        r.getSourceClass().getClassName(),
                        r.getTargetClass().getClassName(),
                        r.getRelationshipType()
                        ))
                .toList();
        return DomainModelDTO.builder()
                .classes(cc)
                .relationships(cr)
                .build();
    }

    private List<Long> parseStoryIds(String storyIdsJson) {
        if (storyIdsJson == null || storyIdsJson.isBlank()) {
            return List.of();
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(storyIdsJson, new TypeReference<List<Long>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Invalid storyIds format: " + storyIdsJson, e);
        }
    }

}
