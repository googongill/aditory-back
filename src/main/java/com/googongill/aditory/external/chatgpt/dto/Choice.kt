package com.googongill.aditory.external.chatgpt.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

data class Choice(
    val index: Int,
    val message: Message,
    @JsonProperty("finish_reason")
    val finishReason: String
) : Serializable