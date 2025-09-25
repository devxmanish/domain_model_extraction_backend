package com.devxmanish.DomainModelExtraction.services.llm;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.output.Response;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LLMSessionManager {

    private final ChatLanguageModel chatModel;
    private final Map<Long, ChatMemory> memories = new ConcurrentHashMap<>();

    public LLMSessionManager(ChatLanguageModel chatModel) {
        this.chatModel = chatModel;
    }

    /**
     * Executes a chat turn for a given job ID.
     * It retrieves the conversation memory, adds the new user message,
     * gets a response from the model, updates the memory, and returns the response.
     *
     * @param jobId The unique identifier for the conversation session.
     * @param userMessage The message from the user.
     * @return The response from the language model.
     */
    public String chat(Long jobId, String userMessage) {
        // Retrieve or create a new ChatMemory for the given job ID.
        // MessageWindowChatMemory keeps the last 10 messages.
        ChatMemory chatMemory = memories.computeIfAbsent(jobId,
                id -> MessageWindowChatMemory.withMaxMessages(10));

        // Add the new user message to the conversation memory.
        chatMemory.add(UserMessage.from(userMessage));

        // Generate a response from the model using the entire conversation history.
        Response<AiMessage> modelResponse = chatModel.generate(chatMemory.messages());

        // Add the AI's response to the conversation memory.
        AiMessage aiMessage = modelResponse.content();
        chatMemory.add(aiMessage);

        return aiMessage.text();
    }

    public void closeSession(Long jobId) {
        memories.remove(jobId);
    }
}