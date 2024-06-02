package com.googongill.aditory.service;

import com.googongill.aditory.controller.dto.search.SearchRequest;
import com.googongill.aditory.domain.Link;
import com.googongill.aditory.domain.User;
import com.googongill.aditory.domain.enums.CategoryScope;
import com.googongill.aditory.domain.enums.CategoryState;
import com.googongill.aditory.domain.enums.SearchType;
import com.googongill.aditory.exception.SearchException;
import com.googongill.aditory.exception.UserException;
import com.googongill.aditory.repository.CategoryRepository;
import com.googongill.aditory.repository.LinkRepository;
import com.googongill.aditory.repository.UserRepository;
import com.googongill.aditory.service.dto.category.CategoryInfo;
import com.googongill.aditory.service.dto.category.CategoryListResult;
import com.googongill.aditory.service.dto.search.SearchResult;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
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

    public SearchResult search(SearchRequest searchRequest, Long userId) {
        // user 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(USER_NOT_FOUND));
        if (searchRequest.getQuery() == null || searchRequest.getQuery().isBlank()) {
            throw new SearchException(EMPTY_QUERY);
        }

        // 검색 타입에 따라 검색
        if (searchRequest.getSearchType() == SearchType.CATEGORY_NAME_SEARCH) {
            return  searchCategory(searchRequest, user.getId());
        } else if (searchRequest.getSearchType() == SearchType.LINK_NAME_SEARCH) {
            return searchLink(searchRequest, user.getId());
        } else if (searchRequest.getSearchType() == SearchType.CATEGORY_NAME_AND_LINK_NAME_SEARCH) {
            return searchCategoryAndLink(searchRequest, user.getId());
        } else {
            throw new SearchException(INVALID_SEARCH_TYPE);
        }
    }
    // 카테고리 내 검색
    private SearchResult searchCategory(SearchRequest searchRequest, Long userId) {
        // user 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(USER_NOT_FOUND));
        // 공유 카테고리 내에서 검색 시 공유 카테고리 이름으로 검색
        if (searchRequest.getCategoryScope() == CategoryScope.IN_PUBLIC) {
            List<CategoryInfo> categoryInfoList = categoryRepository.findByAsCategoryNameContaining(searchRequest.getQuery()).stream()
                    .filter(category -> category.getCategoryState() == CategoryState.PUBLIC)
                    .map(category -> CategoryInfo.builder()
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
                            .build())
                    .collect(Collectors.toList());
            if (categoryInfoList.isEmpty()) {
                throw new SearchException(SEARCH_NOT_FOUND);
            }
            return CategoryListResult.of(categoryInfoList);
        } else if (searchRequest.getCategoryScope() == CategoryScope.IN_MY) {
            // 내 카테고리 내에서 검색 시 카테고리 이름으로 검색
            List<CategoryInfo> myCategoryInfoList = user.getCategories().stream()
                    .filter(category -> category.getCategoryName().contains(searchRequest.getQuery()))
                    .map(category -> CategoryInfo.builder()
                            .categoryId(category.getId())
                            .categoryName(category.getCategoryName())
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
                            .build())
                    .collect(Collectors.toList());
            if (myCategoryInfoList.isEmpty()) {
                throw new SearchException(SEARCH_NOT_FOUND);
            }
            return CategoryListResult.of(myCategoryInfoList);
        } else {
            throw new SearchException(INVALID_CATEGORY_SCOPE);
        }
    }
    //링크 내 검색
    private SearchResult searchLink(SearchRequest searchRequest, Long userId) {
        // user 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(USER_NOT_FOUND));
        if (searchRequest.getCategoryScope() == CategoryScope.IN_PUBLIC) {
            List<CategoryInfo> categoryInfoList = linkRepository.findByTitleContaining(searchRequest.getQuery()).stream()
                    .map(Link::getCategory)
                    .distinct()
                    .filter(category -> category.getCategoryState() == CategoryState.PUBLIC)
                    .map(category -> CategoryInfo.builder()
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
                            .build())
                    .collect(Collectors.toList());
            if (categoryInfoList.isEmpty()) {
                throw new SearchException(SEARCH_NOT_FOUND);
            }
            return CategoryListResult.of(categoryInfoList);
        } else if (searchRequest.getCategoryScope() == CategoryScope.IN_MY) {
            List<CategoryInfo> myCategoryInfoList = linkRepository.findByTitleContaining(searchRequest.getQuery()).stream()
                    .map(Link::getCategory)
                    .distinct()
                    .filter(category -> user.getCategories().contains(category))
                    .map(category -> CategoryInfo.builder()
                            .categoryId(category.getId())
                            .categoryName(category.getCategoryName())
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
                            .build())
                    .collect(Collectors.toList());
            if (myCategoryInfoList.isEmpty()) {
                throw new SearchException(SEARCH_NOT_FOUND);
            }
            return CategoryListResult.of(myCategoryInfoList);
        } else {
            throw new SearchException(INVALID_CATEGORY_SCOPE);
        }
    }
    // 카테고리와 링크의 포함결과로 검색
    private SearchResult searchCategoryAndLink(SearchRequest searchRequest, Long userId) {
        // user 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(USER_NOT_FOUND));
        SearchResult categorySearchResult = searchCategory(searchRequest, userId);
        SearchResult linkSearchResult = searchLink(searchRequest, userId);
        // 공유 카테고리 내에서 카테고리와 링크 검색
        if (categorySearchResult instanceof CategoryListResult && linkSearchResult instanceof CategoryListResult) {
            List<CategoryInfo> categoryInfoList = ((CategoryListResult) categorySearchResult).getCategoryList();
            List<CategoryInfo> linkCategoryInfoList = ((CategoryListResult) linkSearchResult).getCategoryList();
            //두 검색결과의 중복을 제거하여 합침
            categoryInfoList.addAll(linkCategoryInfoList);
            categoryInfoList = categoryInfoList.stream()
                    .distinct()
                    .collect(Collectors.toList());
            if (categoryInfoList.isEmpty()) {
                throw new SearchException(SEARCH_NOT_FOUND);
            }
            return CategoryListResult.of(categoryInfoList);
        } else if (categorySearchResult instanceof CategoryListResult && linkSearchResult instanceof CategoryListResult) {
            // 내 카테고리 내에서 카테고리와 링크 검색
            List<CategoryInfo> myCategoryInfoList = ((CategoryListResult) categorySearchResult).getCategoryList();
            List<CategoryInfo> linkMyCategoryInfoList = ((CategoryListResult) linkSearchResult).getCategoryList();
            // 두 검색결과 중복을 제거하여 합침
            myCategoryInfoList.addAll(linkMyCategoryInfoList);
            myCategoryInfoList = myCategoryInfoList.stream()
                    .distinct()
                    .collect(Collectors.toList());
            if (myCategoryInfoList.isEmpty()) {
                throw new SearchException(SEARCH_NOT_FOUND);
            }
            return CategoryListResult.of(myCategoryInfoList);
        } else {
            throw new SearchException(INVALID_SEARCH_RESULT_TYPE);
        }
    }
}