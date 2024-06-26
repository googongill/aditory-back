package com.googongill.aditory.domain;

import com.googongill.aditory.domain.enums.CategoryState;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category extends BaseEntity {
    @Id @Column(name = "category_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String categoryName;
    private String asCategoryName;
    @Enumerated(EnumType.STRING)
    private CategoryState categoryState;
    private Integer viewCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Link> links = new ArrayList<>();

    @OneToMany(mappedBy = "category", orphanRemoval = true)
    private List<CategoryLike> categoryLikes = new ArrayList<>();

    public Category(String categoryName) {
        this.categoryName = categoryName;
    }

    public Category(String categoryName, String asCategoryName, User user) {
        this.categoryName = categoryName;
        this.asCategoryName = asCategoryName;
        this.viewCount = 0;
        this.categoryState = CategoryState.PRIVATE;
        this.user = user;
    }

    // 연관관계 메서드
    public void setUser(User user) {
        this.user = user;
    }

    public void addLink(Link link) {
        this.links.add(link);
        link.setCategory(this);
    }

    public void addCategoryLike(CategoryLike categoryLike) {
        this.categoryLikes.add(categoryLike);
        categoryLike.setCategory(this);
    }

    public void deleteCategoryLike(CategoryLike categoryLike) {
        this.categoryLikes.remove(categoryLike);
    }

    public void updateCategoryInfo(String categoryName, String asCategoryName, CategoryState categoryState) {
        this.categoryName = categoryName;
        this.asCategoryName = asCategoryName;
        this.categoryState = categoryState;
    }
}
