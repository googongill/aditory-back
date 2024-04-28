package com.googongill.aditory.service.dto.link;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class LinkInfo {
    private Long linkId;
    private String title;
    private String summary;
    private Boolean status;
    private LocalDateTime createdAt;
    private LocalDateTime lastModifiedAt;
}
