package com.googongill.aditory.external.chatgpt.dto

import java.io.Serializable

data class Message(
    val role: String,
    val content: String
) : Serializable
