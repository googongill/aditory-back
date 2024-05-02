package com.googongill.aditory.service;

import com.googongill.aditory.controller.dto.link.CreateLinkRequest;
import com.googongill.aditory.controller.dto.link.UpdateLinkRequest;
import com.googongill.aditory.domain.Category;
import com.googongill.aditory.domain.Link;
import com.googongill.aditory.domain.User;
import com.googongill.aditory.exception.CategoryException;
import com.googongill.aditory.exception.LinkException;
import com.googongill.aditory.exception.UserException;
import com.googongill.aditory.external.chatgpt.ChatGptService;
import com.googongill.aditory.external.chatgpt.dto.AutoCategorizeResult;
import com.googongill.aditory.repository.CategoryRepository;
import com.googongill.aditory.repository.LinkRepository;
import com.googongill.aditory.repository.UserRepository;
import com.googongill.aditory.service.dto.link.LinkResult;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.googongill.aditory.common.code.CategoryErrorCode.CATEGORY_NOT_FOUND;
import static com.googongill.aditory.common.code.CategoryErrorCode.FORBIDDEN_CATEGORY;
import static com.googongill.aditory.common.code.LinkErrorCode.FORBIDDEN_LINK;
import static com.googongill.aditory.common.code.LinkErrorCode.LINK_NOT_FOUND;
import static com.googongill.aditory.common.code.UserErrorCode.USER_NOT_FOUND;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class LinkService {

    private final UserRepository userRepository;
    private final LinkRepository linkRepository;
    private final CategoryRepository categoryRepository;
    private final ChatGptService chatGptService;

    public LinkResult createLink(CreateLinkRequest createLinkRequest, Long userId) {
        if (createLinkRequest.isAutoComplete()) {
            return getAutoCreateLinkResult(createLinkRequest, userId);
        } else {
            return getCreateLinkResult(createLinkRequest, userId);
        }
    }

    private LinkResult getAutoCreateLinkResult(CreateLinkRequest createLinkRequest, Long userId) {
        // 사용자 카테고리 이름 목록 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(USER_NOT_FOUND));
        List<String> userCategoryNameList = user.getCategories().stream()
                .map(Category::getCategoryName)
                .collect(Collectors.toList());
        // chat-gpt 에 url, 카테고리 목록으로 자동 요약 및 분류 결과 조회
        AutoCategorizeResult autoCategorizeResult = chatGptService.autoCategorizeLink(
                createLinkRequest.getUrl(), userCategoryNameList);
        // 해당 카테고리 조회
        Category category = categoryRepository.findByCategoryName(autoCategorizeResult.getCategoryName())
                .orElseThrow(() -> new CategoryException(CATEGORY_NOT_FOUND));
        // 링크 생성
        Link createdLink = linkRepository.save(createLinkRequest.toEntity(autoCategorizeResult, category));
        // 링크 추가 (연관관계 메서드)
        category.addLink(createdLink);
        // 링크 생성 결과
        return LinkResult.of(createdLink, category);
    }

    private LinkResult getCreateLinkResult(CreateLinkRequest createLinkRequest, Long userId) {
        // 카테고리 조회
        Category category = categoryRepository.findById(createLinkRequest.getCategoryId())
                .orElseThrow(() -> new CategoryException(CATEGORY_NOT_FOUND));
        if (!category.getUser().getId().equals(userId)) {
            throw new CategoryException(FORBIDDEN_CATEGORY);
        }
        // 링크 생성
        Link createdLink = linkRepository.save(createLinkRequest.toEntity(category));
        // 링크 추가 (연관관계 메서드)
        category.addLink(createdLink);
        // 링크 생성 결과
        return LinkResult.of(createdLink, category);
    }

    public LinkResult updateLink(Long linkId, UpdateLinkRequest updateLinkRequest, Long userId) {
        // 링크 조회
        Link link = linkRepository.findById(linkId)
                .orElseThrow(() -> new LinkException(LINK_NOT_FOUND));
        if (!link.getCategory().getUser().getId().equals(userId)) {
            throw new LinkException(FORBIDDEN_LINK);
        }
        // 카테고리 조회
        Category category = categoryRepository.findById(updateLinkRequest.getCategoryId())
                .orElseThrow(() -> new CategoryException(FORBIDDEN_CATEGORY));
        // 링크 정보 수정 (연관관계 메서드)
        link.updateLinkInfo(updateLinkRequest.getTitle(), updateLinkRequest.getSummary(), updateLinkRequest.getUrl(), category);
        linkRepository.save(link);
        return LinkResult.of(link, category);
    }
}
