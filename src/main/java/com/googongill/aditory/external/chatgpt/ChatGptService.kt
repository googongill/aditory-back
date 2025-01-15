package com.googongill.aditory.external.chatgpt

import com.googongill.aditory.external.chatgpt.dto.AutoCategorizeResult
import com.googongill.aditory.external.chatgpt.dto.ChatGptRequest
import com.googongill.aditory.external.chatgpt.dto.ChatGptResponse
import com.googongill.aditory.external.chatgpt.dto.Message
import jakarta.transaction.Transactional
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
//@Transactional
class ChatGptService(
    private val chatGptConfig: ChatGptConfig
) {
    private val restTemplate = RestTemplate()

    private fun buildHttpEntity(chatGptRequest: ChatGptRequest): HttpEntity<ChatGptRequest> {
        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            set("charset", "UTF-8")
            add(HttpHeaders.AUTHORIZATION, "Bearer ${chatGptConfig.apiKey}")
        }

        return HttpEntity(chatGptRequest, headers)
    }

    private fun getResponse(chatGptRequestHttpEntity: HttpEntity<ChatGptRequest>?): ChatGptResponse {
        val responseEntity = restTemplate.postForEntity(
            chatGptConfig.url,
            chatGptRequestHttpEntity,
            ChatGptResponse::class.java
        )

        return responseEntity.body
    }

    fun autoCategorizeLink(url: String, userCategoryNameList: List<String>): AutoCategorizeResult {
        // message-content 생성
        val messageContent = createMessageContent(url, userCategoryNameList)
        // messages 생성
        val messages = createMessages(messageContent)
        // http entity 생성
        val chatGptRequestHttpEntity = buildHttpEntity(
            ChatGptRequest(
                model = chatGptConfig.model,
                messages = messages,
                maxTokens = chatGptConfig.maxToken,
                temperature = chatGptConfig.temperature,
                topP = chatGptConfig.topP,
            )
        )
        // response 수신
        val chatGptResponse = getResponse(chatGptRequestHttpEntity)

        // 자동 분류 결과 반환
        return AutoCategorizeResult.of(chatGptResponse)
    }

    companion object {
        private fun createMessages(messageContent: String): List<Message> {
            return listOf(
                Message(
                    role = "user",
                    content = messageContent
                )
            )
        }

        private fun createMessageContent(url: String, userCategoryNameList: List<String>): String {
            val categories = userCategoryNameList.joinToString(", ")
            return "$url summarizing it in one line with a 'summary', a fitting 'title', " +
                    "and selecting the most appropriate category among $categories, returning it as a JSON object named 'category'."
        }
    }
}
