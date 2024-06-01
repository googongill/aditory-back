package com.googongill.aditory.service.dto.category;

import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
public class LikeCategoryListResult {
    @Builder.Default
    List<Long> likeCategoryList = new ArrayList<>();

    public static LikeCategoryListResult of(List<Long> likeCategoryList) {
        return LikeCategoryListResult.builder()
                .likeCategoryList(likeCategoryList)
                .build();
    }
}
