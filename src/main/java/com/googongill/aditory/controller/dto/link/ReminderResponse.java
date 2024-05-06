package com.googongill.aditory.controller.dto.link;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.googongill.aditory.service.dto.link.LinkInfo;
import com.googongill.aditory.service.dto.link.ReminderResult;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;

@Builder
@JsonSerialize
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ReminderResponse {
    @Builder.Default
    private List<LinkInfo> linkList = new ArrayList<>();

    public static ReminderResponse of(ReminderResult reminderResult) {
        return ReminderResponse.builder()
                .linkList(reminderResult.getLinkList())
                .build();
    }
}
