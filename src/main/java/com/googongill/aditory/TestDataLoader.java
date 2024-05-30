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
                "tester nickname",
                "010-1234-5678");
        User testAdmin = new User("testAdmin",
                bCryptPasswordEncoder.encode("testadminpw"),
                Role.ROLE_ADMIN, SocialType.LOCAL,
                "test admin nickname",
                "010-1234-5678");
        userRepository.saveAll(Arrays.asList(testUser, testAdmin));

        Category category1 = new Category("development", testUser);
        Category category2 = new Category("information", testUser);
        categoryRepository.saveAll(Arrays.asList(category1, category2));

        Link link1 = new Link("C++ library", "How to use C++ library's function", "https://www.entertainment.com", category1, testUser);
        Link link2 = new Link("test", "strategy for test", "https://www.test.com", category1, testAdmin);
        Link link3 = new Link("news", "summary for news", "https://www.news.com", category2, testUser);
        linkRepository.saveAll(Arrays.asList(link1, link2, link3));
    }
}