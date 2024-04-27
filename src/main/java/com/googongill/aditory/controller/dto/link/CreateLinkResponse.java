package com.googongill.aditory.controller.dto.link;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.googongill.aditory.service.dto.link.CreateLinkResult;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
@JsonSerialize
public class CreateLinkResponse {
    @JsonProperty("linkId")
    private Long linkId;
    @JsonProperty("categoryId")
    private Long categoryId;
    @JsonProperty("createdAt")
    private LocalDateTime createdAt;

    public static CreateLinkResponse of(CreateLinkResult createLinkResult) {
        return CreateLinkResponse.builder()
                .linkId(createLinkResult.getLinkId())
                .categoryId(createLinkResult.getCategoryId())
                .createdAt(createLinkResult.getCreatedAt())
                .build();
    }
}
