package com.googongill.aditory.service.dto.category;

import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
@Getter
@Builder
public class CategoryPublicListResult {
    List<CategoryPublicInfo> categoryPublicList = new ArrayList<>();

    public static CategoryPublicListResult of(List<CategoryPublicInfo> categoryPublicList) {
        return CategoryPublicListResult.builder()
                .categoryPublicList(categoryPublicList)
                .build();
    }
}