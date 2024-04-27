package com.googongill.aditory.external.chatgpt.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;

@Getter
@Builder
public class ChatGptRequest implements Serializable {
    private String model;
    private List<Message> messages;
    @JsonProperty("max_tokens")
    private Integer maxTokens;
    private Double temperature;
    @JsonProperty("top_p")
    private Double topP;
}
