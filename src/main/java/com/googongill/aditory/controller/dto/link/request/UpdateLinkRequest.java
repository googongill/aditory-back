package com.googongill.aditory.controller.dto.link.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateLinkRequest {
    @NotBlank
    private String title;
    @NotBlank
    private String summary;
    @NotBlank
    private String url;
    @NotNull
    private Long categoryId;
}
