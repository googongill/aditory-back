package com.googongill.aditory.service.dto.category;

import com.googongill.aditory.domain.Category;
import com.googongill.aditory.domain.enums.CategoryState;
import com.googongill.aditory.service.dto.link.LinkInfo;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
public class CategoryDetailResult {
    private Long categoryId;
    private String categoryName;
    private String asCategoryName;
    private Integer linkCount;
    private CategoryState categoryState;
    @Builder.Default
    private List<LinkInfo> linkList = new ArrayList<>();
    private LocalDateTime createdAt;
    private LocalDateTime lastModifiedAt;

    public static CategoryDetailResult of(Category category, List<LinkInfo> linkList) {
        return CategoryDetailResult.builder()
                .categoryId(category.getId())
                .categoryName(category.getCategoryName())
                .asCategoryName(category.getAsCategoryName())
                .linkCount(category.getLinks().size())
                .categoryState(category.getCategoryState())
                .linkList(linkList)
                .createdAt(category.getCreatedAt())
                .lastModifiedAt(category.getLastModifiedAt())
                .build();
    }
}
