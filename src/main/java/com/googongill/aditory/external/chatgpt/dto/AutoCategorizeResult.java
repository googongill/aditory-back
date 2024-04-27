package com.googongill.aditory.external.chatgpt.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AutoCategorizeResult {
    private String title;
    private String summary;
    private String categoryName;

    public static AutoCategorizeResult of(ChatGptResponse chatGptResponse) {
        String jsonString = chatGptResponse.getChoices().get(0).getMessage().getContent();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = null;
        try {
            rootNode = objectMapper.readTree(jsonString);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return AutoCategorizeResult.builder()
                .title(rootNode.get("title").asText())
                .summary(rootNode.get("summary").asText())
                .categoryName(rootNode.get("category").asText())
                .build();
    }
}
