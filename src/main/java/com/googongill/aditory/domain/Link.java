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
    private Boolean linkState;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public Link(String title, String summary, String url, Category category, User user) {
        this.title = title;
        this.summary = summary;
        this.url = url;
        this.linkState = false;
        this.category = category;
        this.user = user;
    }

    public Link(String title, String url, Boolean linkState, Category category, User user) {
        this.title = title;
        this.url = url;
        this.linkState = linkState;
        this.category = category;
        this.user = user;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void updateLinkInfo(String title, String summary, String url, Category category) {
        this.title = title;
        this.summary = summary;
        this.url = url;
        this.category = category;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void updateLinkState() {
        this.linkState = true;
    }
}
