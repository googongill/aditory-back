package com.googongill.aditory.controller.dto.link;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.googongill.aditory.service.dto.link.CreateLinkResult;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
@JsonSerialize
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class CreateLinkResponse {
    private Long linkId;
    private Long categoryId;
    private LocalDateTime createdAt;

    public static CreateLinkResponse of(CreateLinkResult createLinkResult) {
        return CreateLinkResponse.builder()
                .linkId(createLinkResult.getLinkId())
                .categoryId(createLinkResult.getCategoryId())
                .createdAt(createLinkResult.getCreatedAt())
                .build();
    }
}
