package com.devxmanish.DomainModelExtraction.utility;

import org.springframework.core.io.ClassPathResource;
import java.nio.charset.StandardCharsets;
import java.io.IOException;

public class PromptLoader {

    public static String loadPrompt(String fileName) {
        try {
            ClassPathResource resource = new ClassPathResource("prompts" + fileName);
            byte[] data = resource.getInputStream().readAllBytes();
            return new String(data, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load prompt file: " + fileName, e);
        }
    }
}
