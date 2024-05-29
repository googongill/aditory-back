package com.googongill.aditory.service.dto.category;

import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
@Getter
@Builder
public class PublicCategoryListResult {
    @Builder.Default
    List<PublicCategoryInfo> publicCategoryList = new ArrayList<>();

    public static PublicCategoryListResult of(List<PublicCategoryInfo> categoryPublicList) {
        return PublicCategoryListResult.builder()
                .publicCategoryList(categoryPublicList)
                .build();
    }
}