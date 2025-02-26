package com.googongill.aditory.external.chatgpt.dto

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper

data class AutoCategorizeResult(
    val title: String,
    val summary: String,
    val categoryName: String
) {

    companion object {
        fun of(chatGptResponse: ChatGptResponse): AutoCategorizeResult {
            val jsonString = chatGptResponse.choices[0].message.content
            val objectMapper = ObjectMapper()

            var rootNode: JsonNode = try {
                objectMapper.readTree(jsonString)
            } catch (e: JsonProcessingException) {
                throw RuntimeException(e)
            }
            return AutoCategorizeResult(
                title = rootNode.get("title").asText(),
                summary = rootNode.get("summary").asText(),
                categoryName = rootNode.get("category").asText()
            )
        }
    }

}
