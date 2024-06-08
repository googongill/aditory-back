package com.googongill.aditory.controller.dto.user.response;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.googongill.aditory.domain.User;
import lombok.Builder;

@Builder
@JsonSerialize
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class UserInfoResponse {
    private Long userId;
    private String username;
    private String nickname;
    private Integer aditoryPower;

    public static UserInfoResponse of(User user) {
        Integer totalLikes = user.getCategories().stream()
                .mapToInt(category -> category.getCategoryLikes().size())
                .sum();
        Integer aditoryPower = user.getCategories().size() + user.getLinks().size() + totalLikes;

        return UserInfoResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .aditoryPower(aditoryPower)
                .build();
    }
}