package com.googongill.aditory.service.dto.category;

import com.googongill.aditory.service.dto.search.SearchResult;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder

public class MyCategoryListResult implements SearchResult {
    @Builder.Default
    List<MyCategoryInfo> categoryList = new ArrayList<>();

    public static MyCategoryListResult of(List<MyCategoryInfo> categoryList) {
        return MyCategoryListResult.builder()
                .categoryList(categoryList)
                .build();
    }
}
