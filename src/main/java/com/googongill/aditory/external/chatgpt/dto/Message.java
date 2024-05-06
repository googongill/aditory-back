package com.googongill.aditory.external.chatgpt.dto;

import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@Getter
@Builder
public class Message implements Serializable {
    private String role;
    private String content;
}
