package com.googongill.aditory.controller.dto.category;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.googongill.aditory.domain.enums.CategoryState;
import com.googongill.aditory.service.dto.category.MyCategoryResult;
import com.googongill.aditory.service.dto.link.LinkInfo;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;

@Builder
@JsonSerialize
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class MoveCategoryResponse {
    private Long categoryId;
    private String categoryName;
    private Integer linkCount;
    private CategoryState categoryState;
    @Builder.Default
    private List<LinkInfo> linkList = new ArrayList<>();

    public static MoveCategoryResponse of(MyCategoryResult myCategoryResult) {
        return MoveCategoryResponse.builder()
                .categoryId(myCategoryResult.getCategoryId())
                .categoryName(myCategoryResult.getCategoryName())
                .linkCount(myCategoryResult.getLinkCount())
                .categoryState(myCategoryResult.getCategoryState())
                .linkList(myCategoryResult.getLinkList())
                .build();
    }
}