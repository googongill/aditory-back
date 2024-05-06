package com.googongill.aditory.service.dto.link;

import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
public class ReminderResult {
    @Builder.Default
    private List<LinkInfo> linkList = new ArrayList<>();

    public static ReminderResult of(List<LinkInfo> linkInfoList) {
        return ReminderResult.builder()
                .linkList(linkInfoList)
                .build();
    }
}
