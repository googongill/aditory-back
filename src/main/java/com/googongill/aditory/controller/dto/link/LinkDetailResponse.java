package com.googongill.aditory.controller.dto.link;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.googongill.aditory.domain.Link;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
@JsonSerialize
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class LinkDetailResponse {
    private Long linkId;
    private String title;
    private String summary;
    private Boolean linkState;
    private String url;
    private Long categoryId;
    private String categoryName;
    private LocalDateTime createdAt;
    private LocalDateTime lastModifiedAt;

    public static LinkDetailResponse of(Link link) {
        return LinkDetailResponse.builder()
                .linkId(link.getId())
                .title(link.getTitle())
                .summary(link.getSummary())
                .linkState(link.getLinkState())
                .url(link.getUrl())
                .categoryId(link.getCategory().getId())
                .categoryName(link.getCategory().getCategoryName())
                .createdAt(link.getCreatedAt())
                .lastModifiedAt(link.getLastModifiedAt())
                .build();
    }
}
