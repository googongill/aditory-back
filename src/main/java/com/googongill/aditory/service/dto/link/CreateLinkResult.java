package com.googongill.aditory.service.dto.link;

import com.googongill.aditory.domain.Category;
import com.googongill.aditory.domain.Link;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CreateLinkResult {
    private Long linkId;
    private Long categoryId;
    private LocalDateTime createdAt;

    public static CreateLinkResult of(Link link, Category category) {
        return CreateLinkResult.builder()
                .linkId(link.getId())
                .categoryId(category.getId())
                .createdAt(link.getCreatedAt())
                .build();
    }
}
