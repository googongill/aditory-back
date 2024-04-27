package com.googongill.aditory.external.chatgpt;

import com.googongill.aditory.external.chatgpt.dto.AutoCategorizeResult;
import com.googongill.aditory.external.chatgpt.dto.ChatGptRequest;
import com.googongill.aditory.external.chatgpt.dto.ChatGptResponse;
import com.googongill.aditory.external.chatgpt.dto.Message;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ChatGptService {

    private static RestTemplate restTemplate = new RestTemplate();

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

    public HttpEntity<ChatGptRequest> buildHttpEntity(ChatGptRequest chatGptRequest) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("charset", "UTF-8");
        headers.add(AUTHORIZATION, "Bearer " + apiKey);
        return new HttpEntity<>(chatGptRequest, headers);
    }

    public ChatGptResponse getResponse(HttpEntity<ChatGptRequest> chatGptRequestHttpEntity) {
        ResponseEntity<ChatGptResponse> responseEntity = restTemplate.postForEntity(
                url,
                chatGptRequestHttpEntity,
                ChatGptResponse.class);
        return responseEntity.getBody();
    }

    public AutoCategorizeResult autoCategorizeLink(String url, List<String> userCategoryNameList) {
        String messageContent = createMessageCotent(url, userCategoryNameList);
        ArrayList<Message> messages = new ArrayList<>();
        messages.add(Message.builder()
                .role("user")
                .content(messageContent)
                .build()
        );
        ChatGptResponse chatGptResponse = getResponse(
                buildHttpEntity(
                        ChatGptRequest.builder()
                                .model(model)
                                .messages(messages)
                                .maxTokens(maxToken)
                                .temperature(temperature)
                                .topP(topP)
                                .build()
                )
        );

        return AutoCategorizeResult.of(chatGptResponse);
    }

    private static String createMessageCotent(String url, List<String> userCategoryNameList) {
        String categories = String.join(", ", userCategoryNameList);
        String messageContent = url + " 를 한 줄로 요약한 summary 와 어울리는 제목인 title, " + categories + " 중에서 가장 어울리는 카테고리를 category 라는 이름의 json 으로 반환해줘";
        return messageContent;
    }
}
