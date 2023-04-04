package com.example.openAIPrac.service;

import com.theokanning.openai.completion.chat.ChatCompletionChunk;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChatService {


    @Value("{openAI.apiToken}")
    private String MY_OPEN_AI_KEY;
    OpenAiService openAiService = new OpenAiService(MY_OPEN_AI_KEY);

    private final ChatMessage systemMessage = new ChatMessage(ChatMessageRole.SYSTEM.value(), "You are a dog and will speak as such.");
    private final ChatMessage userMessage = new ChatMessage(ChatMessageRole.USER.value(), "");

    public String getGptAnswer(String question){
        ArrayList<String> result = new ArrayList<>();

        ChatCompletionRequest chatCompletionRequest = createCompletionRequest(question);
        openAiService.streamChatCompletion(chatCompletionRequest)
                .doOnError(Throwable::printStackTrace)
                .blockingForEach(chunk -> {
                    String content = chunk.getChoices().get(0).getMessage().getContent();
                    result.add(content);
                } );

        return result.stream().filter(Objects::nonNull).collect(Collectors.joining(""));

    }

    public ChatCompletionRequest createCompletionRequest(String question){
        userMessage.setContent(question);
        List<ChatMessage> messages = new ArrayList<>();
        messages.add(systemMessage);
        messages.add(userMessage);
        return ChatCompletionRequest.builder()
                .model("gpt-3.5-turbo")
                .messages(messages)
                .n(1)
                .maxTokens(1000)
                .build();
    }
}
