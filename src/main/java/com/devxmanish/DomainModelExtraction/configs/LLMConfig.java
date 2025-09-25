package com.devxmanish.DomainModelExtraction.configs;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LLMConfig {

    @Bean
    public ChatLanguageModel chatLanguageModel() {
        // Fetch API key from environment or application.properties
        String openRouterApiKey = "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"; // or @Value("${openrouter.api.key}")

        // OpenRouter API URL
        String openRouterApiUrl = "https://openrouter.ai/api/v1";

        // Build the chat model
        return OpenAiChatModel.builder()
                .apiKey(openRouterApiKey)
                .baseUrl(openRouterApiUrl) // Corrected method name from.apiBaseUrl()
                .modelName("deepseek/deepseek-chat-v3.1:free") // Corrected method name from.model()
                .timeout(java.time.Duration.ofSeconds(120))
                .build();
    }
}