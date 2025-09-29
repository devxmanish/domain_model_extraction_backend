package com.devxmanish.DomainModelExtraction.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
// Updated JobEvent class to include additional fields for better frontend tracking
public class JobEvent {
    private Long jobId;
    private String step;
    private String message;
    private Long storyId;          // ID of the story being processed (optional)
    private Integer storyNumber;   // Current story number (1-based)
    private Integer totalStories;  // Total number of stories

    public JobEvent(Long jobId, String step, String message) {
        this.jobId = jobId;
        this.step = step;
        this.message = message;
    }

}
