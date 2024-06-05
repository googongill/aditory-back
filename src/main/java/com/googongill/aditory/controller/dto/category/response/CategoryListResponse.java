package com.googongill.aditory.controller.dto.category.response;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.googongill.aditory.service.dto.category.CategoryInfo;
import com.googongill.aditory.service.dto.category.CategoryListResult;
import lombok.Builder;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

@Builder
@JsonSerialize
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class CategoryListResponse {
    @Builder.Default
    List<CategoryInfo> categoryList = new ArrayList<>();
    private Integer currentPage;
    private Integer totalPages;
    private Long totalItems;

    public static CategoryListResponse of(CategoryListResult categoryListResult) {
        return CategoryListResponse.builder()
                .categoryList(categoryListResult.getCategoryList())
                .currentPage(0)
                .totalPages(1)
                .totalItems(Long.valueOf(categoryListResult.getCategoryList().size()))
                .build();
    }

    public static CategoryListResponse of(Page<CategoryInfo> categoryInfoPage) {
        return CategoryListResponse.builder()
                .categoryList(categoryInfoPage.getContent())
                .currentPage(categoryInfoPage.getNumber())
                .totalPages(categoryInfoPage.getTotalPages())
                .totalItems(categoryInfoPage.getTotalElements())
                .build();
    }
}
