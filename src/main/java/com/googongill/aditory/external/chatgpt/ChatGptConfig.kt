package com.googongill.aditory.external.chatgpt

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
class ChatGptConfig (
    @Value("\${chat-gpt.url}")
    val url: String,
    @Value("\${chat-gpt.key}")
    val apiKey: String,
    @Value("\${chat-gpt.model}")
    val model: String,
    @Value("\${chat-gpt.max_token}")
    val maxToken: Int,
    @Value("\${chat-gpt.temperature}")
    val temperature: Double,
    @Value("\${chat-gpt.topP}")
    val topP: Double
)
