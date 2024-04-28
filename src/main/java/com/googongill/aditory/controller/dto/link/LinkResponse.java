package com.googongill.aditory.controller.dto.link;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.googongill.aditory.domain.Link;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
@JsonSerialize
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class LinkResponse {
    private Long linkId;
    private String title;
    private String summary;
    private Boolean status;
    private String url;
    private LocalDateTime createdAt;
    private LocalDateTime lastModifiedAt;

    public static LinkResponse of(Link link) {
        return LinkResponse.builder()
                .linkId(link.getId())
                .title(link.getTitle())
                .summary(link.getSummary())
                .status(link.getStatus())
                .url(link.getUrl())
                .createdAt(link.getCreatedAt())
                .lastModifiedAt(link.getLastModifiedAt())
                .build();
    }
}
