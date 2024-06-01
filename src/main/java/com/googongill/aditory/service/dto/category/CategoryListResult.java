package com.googongill.aditory.service.dto.category;

import com.googongill.aditory.service.dto.search.SearchResult;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
public class CategoryListResult implements SearchResult {
    @Builder.Default
    List<MyCategoryInfo> categoryList = new ArrayList<>();

    public static CategoryListResult of(List<MyCategoryInfo> categoryList) {
        return CategoryListResult.builder()
                .categoryList(categoryList)
                .build();
    }
}
