package com.googongill.aditory.external.chatgpt;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class ChatGptConfig {
    @Value("${chat-gpt.url}")
    private String url;
    @Value("${chat-gpt.key}")
    private String apiKey;
    @Value("${chat-gpt.model}")
    private String model;
    @Value("${chat-gpt.max_token}")
    private Integer maxToken;
    @Value("${chat-gpt.temperature}")
    private Double temperature;
    @Value("${chat-gpt.topP}")
    private Double topP;
}
