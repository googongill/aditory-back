package com.googongill.aditory.controller.dto.category.response;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.googongill.aditory.domain.enums.CategoryState;
import com.googongill.aditory.service.dto.category.CategoryDetailResult;
import com.googongill.aditory.service.dto.link.LinkInfo;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;

@Builder
@JsonSerialize
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class CategoryDetailResponse {
    private Long categoryId;
    private String categoryName;
    private String asCategoryName;
    private Integer linkCount;
    private Integer likeCount;
    private CategoryState categoryState;
    @Builder.Default
    private List<LinkInfo> linkList = new ArrayList<>();

    public static CategoryDetailResponse of(CategoryDetailResult categoryDetailResult) {
        return CategoryDetailResponse.builder()
                .categoryId(categoryDetailResult.getCategoryId())
                .categoryName(categoryDetailResult.getCategoryName())
                .asCategoryName(categoryDetailResult.getAsCategoryName())
                .linkCount(categoryDetailResult.getLinkCount())
                .likeCount(categoryDetailResult.getLikeCount())
                .categoryState(categoryDetailResult.getCategoryState())
                .linkList(categoryDetailResult.getLinkList())
                .build();
    }
}
