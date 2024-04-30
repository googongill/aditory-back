package com.googongill.aditory.controller.dto.link;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Builder;

@Builder
@JsonSerialize
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class DeleteLinkResponse {
    private Long linkId;

    public static DeleteLinkResponse of(Long linkId) {
        return DeleteLinkResponse.builder()
                .linkId(linkId)
                .build();
    }
}
