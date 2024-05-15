package com.googongill.aditory.service.dto.link;

import com.googongill.aditory.domain.Category;
import com.googongill.aditory.domain.Link;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class LinkResult {
    private Long linkId;
    private Long categoryId;
    private LocalDateTime createdAt;
    private LocalDateTime lastModifiedAt;

    public static LinkResult of(Link link, Category category) {
        return LinkResult.builder()
                .linkId(link.getId())
                .categoryId(category.getId())
                .createdAt(link.getCreatedAt())
                .lastModifiedAt(link.getLastModifiedAt())
                .build();
    }
}
