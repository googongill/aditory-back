package com.googongill.aditory.controller.dto.category;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.googongill.aditory.domain.enums.CategoryState;
import com.googongill.aditory.service.dto.category.CategoryResult;
import com.googongill.aditory.service.dto.link.LinkInfo;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;

@Builder
@JsonSerialize
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class CategoryResponse {
    private Long categoryId;
    private String categoryName;
    private Integer linkCount;
    private CategoryState categoryState;
    private List<LinkInfo> linkList = new ArrayList<>();

    public static CategoryResponse of(CategoryResult categoryResult) {
        return CategoryResponse.builder()
                .categoryId(categoryResult.getCategoryId())
                .categoryName(categoryResult.getCategoryName())
                .linkCount(categoryResult.getLinkCount())
                .categoryState(categoryResult.getCategoryState())
                .linkList(categoryResult.getLinkList())
                .build();
    }
}
