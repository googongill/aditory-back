package com.googongill.aditory.domain;

import com.googongill.aditory.domain.enums.Role;
import com.googongill.aditory.domain.enums.SocialType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {
    @Id @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private SocialType socialType;
    private String socialId;

    private String nickname;
    private String contact;

    private String refreshToken;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Category> categories = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Link> links = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "profile_image_id")
    private ProfileImage profileImage;

    public User(String username, String password, Role role, SocialType socialType, String nickname, String contact) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.socialType = socialType;
        this.nickname = nickname;
        this.contact = contact;
    }

    public User(SocialType socialType, String socialId, String username, String nickname, Role role) {
        this.socialType = socialType;
        this.socialId = socialId;
        this.username = username;
        this.nickname = nickname;
        this.role = role;
    }

    // 연관관계 메서드
    public void addCategory(Category category) {
        this.categories.add(category);
        category.setUser(this);
    }

    public void addCategories(List<Category> categories) {
        for (Category category : categories) {
            this.categories.add(category);
            category.setUser(this);
        }
    }

    public void addLink(Link link) {
        this.links.add(link);
        link.setUser(this);
    }

    public void updateProfileImage(ProfileImage profileImage) {
        this.profileImage = profileImage;
    }

    public void saveRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void deleteRefreshToken() {
        this.refreshToken = null;
    }

    public void updateUserInfo(String nickname, String contact) {
        this.nickname = nickname;
        this.contact = contact;
    }
}
