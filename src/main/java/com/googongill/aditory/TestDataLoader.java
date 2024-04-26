package com.googongill.aditory;

import com.googongill.aditory.domain.Category;
import com.googongill.aditory.domain.Link;
import com.googongill.aditory.domain.User;
import com.googongill.aditory.domain.enums.Role;
import com.googongill.aditory.domain.enums.SocialType;
import com.googongill.aditory.repository.CategoryRepository;
import com.googongill.aditory.repository.LinkRepository;
import com.googongill.aditory.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class TestDataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final LinkRepository linkRepository;
    private final CategoryRepository categoryRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // 사용자 생성
        User testUser = new User("testuser",
                bCryptPasswordEncoder.encode("testuserpw"),
                Role.ROLE_USER, SocialType.LOCAL,
                "테스트 유저",
                "010-1234-5678");
        User testAdmin = new User("testAdmin",
                bCryptPasswordEncoder.encode("testadminpw"),
                Role.ROLE_ADMIN, SocialType.LOCAL,
                "테스트 어드민",
                "010-1234-5678");
        userRepository.saveAll(Arrays.asList(testUser, testAdmin));

        Category category1 = new Category("뉴스 및 정보", testUser);
        Category category2 = new Category("엔터테인먼트", testUser);
        categoryRepository.saveAll(Arrays.asList(category1, category2));

        Link link1 = new Link("뉴스 1", "뉴스 1에 대한 요약입니다.", "https://www.news1.com", category1);
        Link link2 = new Link("뉴스 2", "뉴스 2에 대한 요약입니다.", "https://www.news1.com", category1);
        Link link3 = new Link("엔터테인먼트 2", "엔터테인먼트 2에 대한 요약입니다.", "https://www.entertainment.com", category2);
        linkRepository.saveAll(Arrays.asList(link1, link2, link3));
    }
}
