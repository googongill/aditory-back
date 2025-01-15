package com.googongill.aditory.controller.dto.link.request

import com.googongill.aditory.domain.Category
import com.googongill.aditory.domain.Link
import com.googongill.aditory.domain.User
import com.googongill.aditory.external.chatgpt.dto.AutoCategorizeResult
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class CreateLinkRequest(
    @NotNull
    val autoComplete: Boolean,
    val title: String? = null,
    val summary: String? = null,
    @NotBlank
    val url:  String,
    val categoryId: Long? = null
) {
    fun toEntity(category: Category?, user: User?): Link {
        return Link(title!!, summary, url, category!!, user!!)
    }

    fun toEntity(autoCategorizeResult: AutoCategorizeResult, category: Category?, user: User?): Link {
        return Link(autoCategorizeResult.title, autoCategorizeResult.summary, url, category!!, user!!)
    }
}
