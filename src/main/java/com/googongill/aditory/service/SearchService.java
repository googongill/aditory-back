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
import com.googongill.aditory.service.dto.category.CategoryListResult;
import com.googongill.aditory.service.dto.category.MyCategoryInfo;
import com.googongill.aditory.service.dto.category.PublicCategoryInfo;
import com.googongill.aditory.service.dto.category.PublicCategoryListResult;
import com.googongill.aditory.service.dto.search.SearchResult;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
            List<PublicCategoryInfo> publicCategoryInfoList = categoryRepository.findByAsCategoryNameContaining(searchRequest.getQuery()).stream()
                    .filter(category -> category.getCategoryState() == CategoryState.PUBLIC)
                    .map(category -> PublicCategoryInfo.builder()
                            .categoryId(category.getId())
                            .categoryName(category.getCategoryName())
                            .asCategoryName(category.getAsCategoryName())
                            .linkCount(category.getLinks().size())
                            .likeCount(category.getCategoryLikes().size())
                            .categoryState(category.getCategoryState())
                            .createdAt(category.getCreatedAt())
                            .lastModifiedAt(category.getLastModifiedAt())
                            .build())
                    .collect(Collectors.toList());
            if (publicCategoryInfoList.isEmpty()) {
                throw new SearchException(SEARCH_NOT_FOUND);
            }
            return PublicCategoryListResult.of(publicCategoryInfoList);
        } else if (searchRequest.getCategoryScope() == CategoryScope.IN_MY) {
            // 내 카테고리 내에서 검색 시 카테고리 이름으로 검색
            List<MyCategoryInfo> myCategoryInfoList = user.getCategories().stream()
                    .filter(category -> category.getCategoryName().contains(searchRequest.getQuery()))
                    .map(category -> MyCategoryInfo.builder()
                            .categoryId(category.getId())
                            .categoryName(category.getCategoryName())
                            .linkCount(category.getLinks().size())
                            .categoryState(category.getCategoryState())
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
            List<PublicCategoryInfo> publicCategoryInfoList = linkRepository.findByTitleContaining(searchRequest.getQuery()).stream()
                    .map(Link::getCategory)
                    .distinct()
                    .filter(category -> category.getCategoryState() == CategoryState.PUBLIC)
                    .map(category -> PublicCategoryInfo.builder()
                            .categoryId(category.getId())
                            .categoryName(category.getCategoryName())
                            .asCategoryName(category.getAsCategoryName())
                            .linkCount(category.getLinks().size())
                            .likeCount(category.getCategoryLikes().size())
                            .categoryState(category.getCategoryState())
                            .createdAt(category.getCreatedAt())
                            .lastModifiedAt(category.getLastModifiedAt())
                            .build())
                    .collect(Collectors.toList());
            if (publicCategoryInfoList.isEmpty()) {
                throw new SearchException(SEARCH_NOT_FOUND);
            }
            return PublicCategoryListResult.of(publicCategoryInfoList);
        } else if (searchRequest.getCategoryScope() == CategoryScope.IN_MY) {
            List<MyCategoryInfo> myCategoryInfoList = linkRepository.findByTitleContaining(searchRequest.getQuery()).stream()
                    .map(Link::getCategory)
                    .distinct()
                    .filter(category -> user.getCategories().contains(category))
                    .map(category -> MyCategoryInfo.builder()
                            .categoryId(category.getId())
                            .categoryName(category.getCategoryName())
                            .linkCount(category.getLinks().size())
                            .categoryState(category.getCategoryState())
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
        if (categorySearchResult instanceof PublicCategoryListResult && linkSearchResult instanceof PublicCategoryListResult) {
            List<PublicCategoryInfo> publicCategoryInfoList = ((PublicCategoryListResult) categorySearchResult).getPublicCategoryList();
            List<PublicCategoryInfo> linkPublicCategoryInfoList = ((PublicCategoryListResult) linkSearchResult).getPublicCategoryList();
            //두 검색결과의 중복을 제거하여 합침
            publicCategoryInfoList.addAll(linkPublicCategoryInfoList);
            publicCategoryInfoList = publicCategoryInfoList.stream()
                    .distinct()
                    .collect(Collectors.toList());
            if (publicCategoryInfoList.isEmpty()) {
                throw new SearchException(SEARCH_NOT_FOUND);
            }
            return PublicCategoryListResult.of(publicCategoryInfoList);
        } else if (categorySearchResult instanceof CategoryListResult && linkSearchResult instanceof CategoryListResult) {
            // 내 카테고리 내에서 카테고리와 링크 검색
            List<MyCategoryInfo> myCategoryInfoList = ((CategoryListResult) categorySearchResult).getCategoryList();
            List<MyCategoryInfo> linkMyCategoryInfoList = ((CategoryListResult) linkSearchResult).getCategoryList();
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