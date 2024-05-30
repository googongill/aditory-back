package com.googongill.aditory.service.dto.category;

import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
public class MyCategoryListResult {
    @Builder.Default
    List<MyCategoryInfo> categoryList = new ArrayList<>();

    public static MyCategoryListResult of(List<MyCategoryInfo> categoryList) {
        return MyCategoryListResult.builder()
                .categoryList(categoryList)
                .build();
    }
}
