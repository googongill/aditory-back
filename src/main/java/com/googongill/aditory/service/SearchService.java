package com.googongill.aditory.service;

import com.googongill.aditory.controller.dto.search.SearchRequest;
import com.googongill.aditory.domain.Category;
import com.googongill.aditory.domain.Link;
import com.googongill.aditory.domain.User;
import com.googongill.aditory.domain.enums.CategoryScope;
import com.googongill.aditory.domain.enums.CategoryState;
import com.googongill.aditory.exception.SearchException;
import com.googongill.aditory.exception.UserException;
import com.googongill.aditory.repository.CategoryRepository;
import com.googongill.aditory.repository.LinkRepository;
import com.googongill.aditory.repository.UserRepository;
import com.googongill.aditory.service.dto.category.CategoryInfo;
import com.googongill.aditory.service.dto.link.LinkInfo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.stream.Collectors;

import static com.googongill.aditory.common.code.SearchErrorCode.*;
import static com.googongill.aditory.common.code.UserErrorCode.USER_NOT_FOUND;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class SearchService {

    private final UserRepository userRepository;
    private final LinkRepository linkRepository;
    private final CategoryRepository categoryRepository;

    public Page<CategoryInfo> searchCategories(SearchRequest searchRequest, Pageable pageable, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(USER_NOT_FOUND));
        String query = searchRequest.getQuery();
        CategoryScope categoryScope = searchRequest.getCategoryScope();

        if (categoryScope.equals(CategoryScope.IN_MY)) {
            return searchByCategoryName(query, pageable, user, null);
        } else if (categoryScope.equals(CategoryScope.IN_PUBLIC)) {
            return searchByCategoryName(query, pageable, null, CategoryState.PUBLIC);
        } else {
            throw new SearchException(INVALID_SEARCH_TYPE);
        }
    }

    private Page<CategoryInfo> searchByCategoryName(String query, Pageable pageable, User user, CategoryState categoryState) {
        Page<Category> categories;

        if (user != null) {
            categories = categoryRepository.findByCategoryNameContainingAndUser(query, user, pageable);
        } else {
            categories = categoryRepository.findByAsCategoryNameContainingAndCategoryState(query, categoryState, pageable);
        }

        return categories.map(category -> CategoryInfo.builder()
                .categoryId(category.getId())
                .categoryName(category.getCategoryName())
                .asCategoryName(category.getAsCategoryName())
                .linkCount(category.getLinks().size())
                .likeCount(category.getCategoryLikes().size())
                .categoryState(category.getCategoryState())
                .prevLinks(category.getLinks().stream()
                        .sorted(Comparator.comparing(Link::getCreatedAt).reversed())
                        .limit(4)
                        .map(Link::getUrl)
                        .collect(Collectors.toList())
                )
                .createdAt(category.getCreatedAt())
                .lastModifiedAt(category.getLastModifiedAt())
                .build());
    }

    public Page<LinkInfo> searchLinks(SearchRequest searchRequest, Pageable pageable, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(USER_NOT_FOUND));
        String query = searchRequest.getQuery();
        CategoryScope categoryScope = searchRequest.getCategoryScope();

        if (categoryScope.equals(CategoryScope.IN_MY)) {
            return searchByLinkTitle(query, pageable, user, null);
        } else if (categoryScope.equals(CategoryScope.IN_PUBLIC)) {
            return searchByLinkTitle(query, pageable, null, CategoryState.PUBLIC);
        } else {
            throw new SearchException(INVALID_CATEGORY_SCOPE);
        }
    }

    private Page<LinkInfo> searchByLinkTitle(String query, Pageable pageable, User user, CategoryState categoryState) {
        Page<Link> links;

        if (user != null) {
            links = linkRepository.findByTitleContainingAndUser(query, user, pageable);
        } else {
            links = linkRepository.findByTitleContainingAndCategory_CategoryState(query, categoryState, pageable);
        }

        return links.map(link -> LinkInfo.builder()
                .linkId(link.getId())
                .title(link.getTitle())
                .summary(link.getSummary())
                .url(link.getUrl())
                .linkState(link.getLinkState())
                .createdAt(link.getCreatedAt())
                .lastModifiedAt(link.getLastModifiedAt())
                .build());
    }
}