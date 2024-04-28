package com.googongill.aditory.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Link extends BaseEntity {
    @Id @Column(name = "link_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String summary;
    private String url;
    private Boolean status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    public Link(String title, String summary, String url, Category category) {
        this.title = title;
        this.summary = summary;
        this.url = url;
        this.status = false;
        this.category = category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
