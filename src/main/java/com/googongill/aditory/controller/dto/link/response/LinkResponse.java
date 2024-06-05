package com.googongill.aditory.controller.dto.link.response;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.googongill.aditory.service.dto.link.LinkResult;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
@JsonSerialize
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class LinkResponse {
    private Long linkId;
    private Long categoryId;
    private LocalDateTime createdAt;
    private LocalDateTime lastModifiedAt;

    public static LinkResponse of(LinkResult linkResult) {
        return LinkResponse.builder()
                .linkId(linkResult.getLinkId())
                .categoryId(linkResult.getCategoryId())
                .createdAt(linkResult.getCreatedAt())
                .lastModifiedAt(linkResult.getLastModifiedAt())
                .build();
    }
}
