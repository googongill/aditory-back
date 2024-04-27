package com.googongill.aditory.service;

import com.googongill.aditory.controller.dto.link.CreateLinkRequest;
import com.googongill.aditory.domain.Category;
import com.googongill.aditory.domain.User;
import com.googongill.aditory.exception.CategoryException;
import com.googongill.aditory.external.chatgpt.ChatGptService;
import com.googongill.aditory.repository.CategoryRepository;
import com.googongill.aditory.repository.LinkRepository;
import com.googongill.aditory.security.jwt.user.PrincipalDetails;
import com.googongill.aditory.service.dto.link.CreateLinkResult;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.googongill.aditory.common.code.CategoryErrorCode.CATEGORY_NOT_FOUND;
import static com.googongill.aditory.common.code.CategoryErrorCode.FORBIDDEN_CATEGORY;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class LinkService {

    private final LinkRepository linkRepository;
    private final CategoryRepository categoryRepository;
    private final ChatGptService chatGptService;

    public CreateLinkResult createLink(CreateLinkRequest createLinkRequest, User user) {
        if (createLinkRequest.isAutoComplete()) {
            List<String> userCategoryNameList = user.getCategories().stream()
                    .map(Category::getCategoryName)
                    .collect(Collectors.toList());
//            chatGptService.createLink();
            return null;
        } else {
            Category category = categoryRepository.findById(createLinkRequest.getCategoryId())
                    .orElseThrow(() -> new CategoryException(CATEGORY_NOT_FOUND));
            if (!category.getUser().getId().equals(user.getId())) {
                throw new CategoryException(FORBIDDEN_CATEGORY);
            }
            return CreateLinkResult.of(linkRepository.save(createLinkRequest.toEntity(category)), category);
        }
    }
}
