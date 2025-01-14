package com.googongill.aditory.service.dto.link

import java.time.LocalDateTime

data class LinkInfo(
    val linkId: Long?,
    val title: String,
    val summary: String?,
    val url: String,
    val linkState: Boolean,
    val createdAt: LocalDateTime?,
    val lastModifiedAt: LocalDateTime?
)