package com.googongill.aditory.controller.dto.link;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.googongill.aditory.service.dto.link.LinkInfo;
import com.googongill.aditory.service.dto.link.LinkListResult;
import lombok.Builder;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

@Builder
@JsonSerialize
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class LinkListResponse {
    @Builder.Default
    private List<LinkInfo> linkList = new ArrayList<>();
    private Integer currentPage;
    private Integer totalPages;
    private Long totalItems;

    public static LinkListResponse of(LinkListResult linkListResult) {
        return LinkListResponse.builder()
                .linkList(linkListResult.getLinkList())
                .currentPage(0)
                .totalPages(1)
                .totalItems(Long.valueOf(linkListResult.getLinkList().size()))
                .build();
    }

    public static LinkListResponse of(Page<LinkInfo> linkInfoPage) {
        return LinkListResponse.builder()
                .linkList(linkInfoPage.getContent())
                .currentPage(linkInfoPage.getNumber())
                .totalPages(linkInfoPage.getTotalPages())
                .totalItems(linkInfoPage.getTotalElements())
                .build();
    }
}
