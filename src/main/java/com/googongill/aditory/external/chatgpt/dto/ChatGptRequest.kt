package com.googongill.aditory.external.chatgpt.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

data class ChatGptRequest(
    val model: String,
    val messages: List<Message>,
    @JsonProperty("max_tokens")
    private val maxTokens: Int,
    private val temperature: Double,
    @JsonProperty("top_p")
    private val topP: Double
) : Serializable
