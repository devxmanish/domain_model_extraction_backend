package com.devxmanish.DomainModelExtraction.controllers;

import com.devxmanish.DomainModelExtraction.dtos.Response;
import com.devxmanish.DomainModelExtraction.services.UMLGenerationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/uml")
public class UMLController {

    @Autowired
    private UMLGenerationService umlGenerationService;

    @GetMapping("/{jobId}/code")
    public ResponseEntity<Response<?>> getPlantUMLCode(@PathVariable Long jobId){
        return ResponseEntity.ok(Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("uml code generated successfully")
                .data(umlGenerationService.generatePlantUML(jobId))
                .build());
    }

}
