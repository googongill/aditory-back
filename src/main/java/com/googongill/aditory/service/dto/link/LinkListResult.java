package com.googongill.aditory.service.dto.link;

import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
public class LinkListResult {
    @Builder.Default
    private List<LinkInfo> linkList = new ArrayList<>();

    public static LinkListResult of(List<LinkInfo> linkInfoList) {
        return LinkListResult.builder()
                .linkList(linkInfoList)
                .build();
    }
}
