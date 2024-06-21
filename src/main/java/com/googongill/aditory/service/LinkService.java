package com.googongill.aditory.service;

import com.googongill.aditory.controller.dto.link.request.CreateLinkRequest;
import com.googongill.aditory.controller.dto.link.request.UpdateLinkRequest;
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
import com.googongill.aditory.service.dto.link.LinkInfo;
import com.googongill.aditory.service.dto.link.LinkResult;
import com.googongill.aditory.service.dto.link.LinkListResult;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.googongill.aditory.common.code.CategoryErrorCode.CATEGORY_NOT_FOUND;
import static com.googongill.aditory.common.code.CategoryErrorCode.CATEGORY_FORBIDDEN;
import static com.googongill.aditory.common.code.LinkErrorCode.*;
import static com.googongill.aditory.common.code.UserErrorCode.USER_NOT_FOUND;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class LinkService {

    private final UserRepository userRepository;
    private final LinkRepository linkRepository;
    private final ChatGptService chatGptService;
    private final CategoryRepository categoryRepository;

    public LinkResult createLink(CreateLinkRequest createLinkRequest, Long userId) {
        if (createLinkRequest.isAutoComplete()) {
            return getAutoCreateLinkResult(createLinkRequest, userId);
        } else {
            return getCreateLinkResult(createLinkRequest, userId);
        }
    }

    private LinkResult getAutoCreateLinkResult(CreateLinkRequest createLinkRequest, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(USER_NOT_FOUND));
        List<String> userCategoryNameList = user.getCategories().stream()
                .map(Category::getCategoryName)
                .collect(Collectors.toList());
        AutoCategorizeResult autoCategorizeResult = chatGptService.autoCategorizeLink(
                createLinkRequest.getUrl(), userCategoryNameList);
        Category category = categoryRepository.findByCategoryNameAndUser(autoCategorizeResult.getCategoryName(), user)
                .orElseThrow(() -> new CategoryException(CATEGORY_NOT_FOUND));
        Link createdLink = linkRepository.save(createLinkRequest.toEntity(autoCategorizeResult, category, user));
        category.addLink(createdLink);
        user.addLink(createdLink);
        return LinkResult.of(createdLink, category);
    }

    private LinkResult getCreateLinkResult(CreateLinkRequest createLinkRequest, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(USER_NOT_FOUND));
        Category category = categoryRepository.findById(createLinkRequest.getCategoryId())
                .orElseThrow(() -> new CategoryException(CATEGORY_NOT_FOUND));
        if (!category.getUser().getId().equals(userId)) {
            throw new CategoryException(CATEGORY_FORBIDDEN);
        }
        Link createdLink = linkRepository.save(createLinkRequest.toEntity(category, user));
        category.addLink(createdLink);
        user.addLink(createdLink);
        return LinkResult.of(createdLink, category);
    }

    public LinkResult updateLink(Long linkId, UpdateLinkRequest updateLinkRequest, Long userId) {
        Link link = linkRepository.findById(linkId)
                .orElseThrow(() -> new LinkException(LINK_NOT_FOUND));
        if (!link.getUser().getId().equals(userId)) {
            throw new LinkException(LINK_FORBIDDEN);
        }
        Category category = categoryRepository.findById(updateLinkRequest.getCategoryId())
                .orElseThrow(() -> new CategoryException(CATEGORY_NOT_FOUND));
        if (!category.getUser().getId().equals(userId)) {
            throw new CategoryException(CATEGORY_FORBIDDEN);
        }
        link.updateLinkInfo(updateLinkRequest.getTitle(), updateLinkRequest.getSummary(), updateLinkRequest.getUrl(), category);
        linkRepository.save(link);
        return LinkResult.of(link, category);
    }

    public LinkListResult getReminder(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(USER_NOT_FOUND));
        List<Link> oldestLinks = linkRepository.findTop10ByUserAndLinkStateOrderByCreatedAtAsc(user, false);
        if (oldestLinks.isEmpty()) {
            throw new LinkException(REMINDER_EMPTY);
        }
        List<LinkInfo> linkInfoList = oldestLinks.stream()
                .map(link -> LinkInfo.builder()
                        .linkId(link.getId())
                        .title(link.getTitle())
                        .summary(link.getSummary())
                        .url(link.getUrl())
                        .linkState(link.getLinkState())
                        .createdAt(link.getCreatedAt())
                        .lastModifiedAt(link.getLastModifiedAt())
                        .build()
                )
                .collect(Collectors.toList());
        return LinkListResult.of(linkInfoList);
    }
}
