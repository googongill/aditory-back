package com.googongill.aditory.external.chatgpt;

import com.googongill.aditory.external.chatgpt.dto.AutoCategorizeResult;
import com.googongill.aditory.external.chatgpt.dto.ChatGptRequest;
import com.googongill.aditory.external.chatgpt.dto.ChatGptResponse;
import com.googongill.aditory.external.chatgpt.dto.Message;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    private final RestTemplate restTemplate = new RestTemplate();
    private final ChatGptConfig chatGptConfig;

    public HttpEntity<ChatGptRequest> buildHttpEntity(ChatGptRequest chatGptRequest) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("charset", "UTF-8");
        headers.add(AUTHORIZATION, "Bearer " + chatGptConfig.getApiKey());
        return new HttpEntity<>(chatGptRequest, headers);
    }

    public ChatGptResponse getResponse(HttpEntity<ChatGptRequest> chatGptRequestHttpEntity) {
        ResponseEntity<ChatGptResponse> responseEntity = restTemplate.postForEntity(
                chatGptConfig.getUrl(),
                chatGptRequestHttpEntity,
                ChatGptResponse.class);
        return responseEntity.getBody();
    }

    private static ArrayList<Message> createMessages(String messageContent) {
        ArrayList<Message> messages = new ArrayList<>();
        messages.add(Message.builder()
                .role("user")
                .content(messageContent)
                .build()
        );
        return messages;
    }

    private static String createMessageContent(String url, List<String> userCategoryNameList) {
        String categories = String.join(", ", userCategoryNameList);
        String messageContent = url + " 를 한 줄로 요약한 summary 와 어울리는 제목인 title, " +
                                categories + " 중에서 가장 어울리는 카테고리를 category 라는 이름의 json 으로 반환해줘";
        return messageContent;
    }

    public AutoCategorizeResult autoCategorizeLink(String url, List<String> userCategoryNameList) {
        // message-content 생성
        String messageContent = createMessageContent(url, userCategoryNameList);
        // messages 생성
        ArrayList<Message> messages = createMessages(messageContent);
        // http entity 생성
        HttpEntity<ChatGptRequest> chatGptRequestHttpEntity = buildHttpEntity(ChatGptRequest.builder()
                .model(chatGptConfig.getModel())
                .messages(messages)
                .maxTokens(chatGptConfig.getMaxToken())
                .temperature(chatGptConfig.getTemperature())
                .topP(chatGptConfig.getTopP())
                .build());
        // response 수신
        ChatGptResponse chatGptResponse = getResponse(chatGptRequestHttpEntity);

        // 자동 분류 결과 반환
        return AutoCategorizeResult.of(chatGptResponse);
    }
}
