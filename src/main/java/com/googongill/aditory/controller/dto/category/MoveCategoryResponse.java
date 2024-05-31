package com.googongill.aditory.controller.dto.category;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.googongill.aditory.domain.enums.CategoryState;
import com.googongill.aditory.service.dto.category.CategoryDetailResult;
import com.googongill.aditory.service.dto.link.LinkInfo;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@JsonSerialize
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class MoveCategoryResponse {
    private Long categoryId;
    private String categoryName;
    private String asCategoryName;
    private Integer linkCount;
    private CategoryState categoryState;
    @Builder.Default
    private List<LinkInfo> linkList = new ArrayList<>();
    private LocalDateTime createdAt;
    private LocalDateTime lastModifiedAt;

    public static MoveCategoryResponse of(CategoryDetailResult categoryDetailResult) {
        return MoveCategoryResponse.builder()
                .categoryId(categoryDetailResult.getCategoryId())
                .categoryName(categoryDetailResult.getCategoryName())
                .asCategoryName(categoryDetailResult.getAsCategoryName())
                .linkCount(categoryDetailResult.getLinkCount())
                .categoryState(categoryDetailResult.getCategoryState())
                .linkList(categoryDetailResult.getLinkList())
                .createdAt(categoryDetailResult.getCreatedAt())
                .lastModifiedAt(categoryDetailResult.getLastModifiedAt())
                .build();
    }
}