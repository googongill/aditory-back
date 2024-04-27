package com.googongill.aditory.controller.dto.link;

import com.googongill.aditory.domain.Category;
import com.googongill.aditory.domain.Link;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateLinkRequest {
    @NotNull
    private boolean autoComplete;
    private String title;
    private String summary;
    @NotBlank
    private String url;
    private Long categoryId;

    public Link toEntity(Category category) {
        return new Link(title, summary, url, category);
    }
}
