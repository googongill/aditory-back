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
public class MyCategoryResult {
    private Long categoryId;
    private String categoryName;
    private Integer linkCount;
    private CategoryState categoryState;
    @Builder.Default
    private List<LinkInfo> linkList = new ArrayList<>();
    private LocalDateTime createdAt;
    private LocalDateTime lastModifiedAt;

    public static MyCategoryResult of(Category category, List<LinkInfo> linkList) {
        return MyCategoryResult.builder()
                .categoryId(category.getId())
                .categoryName(category.getCategoryName())
                .linkCount(category.getLinks().size())
                .categoryState(category.getCategoryState())
                .linkList(linkList)
                .createdAt(category.getCreatedAt())
                .lastModifiedAt(category.getLastModifiedAt())
                .build();
    }
}
