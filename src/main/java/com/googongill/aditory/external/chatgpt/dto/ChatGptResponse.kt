package com.googongill.aditory.external.chatgpt.dto

import java.io.Serializable
import java.time.LocalDate

data class ChatGptResponse(
    val id: String,
    val `object`: String,
    val created: LocalDate,
    val model: String,
    val choices: List<Choice>
) : Serializable
