package com.googongill.aditory.service.dto.category;

import com.googongill.aditory.domain.enums.CategoryState;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
public class CategoryInfo {
    private Long categoryId;
    private String categoryName;
    private String asCategoryName;
    private Integer linkCount;
    private Integer likeCount;
    private CategoryState categoryState;
    @Builder.Default
    private List<String> prevLinks = new ArrayList<>();
    private LocalDateTime createdAt;
    private LocalDateTime lastModifiedAt;
}
