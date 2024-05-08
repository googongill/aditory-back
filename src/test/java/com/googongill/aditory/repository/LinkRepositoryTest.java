package com.googongill.aditory.repository;

import com.googongill.aditory.TestDataRepository;
import com.googongill.aditory.domain.Category;
import com.googongill.aditory.domain.Link;
import com.googongill.aditory.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Arrays;
import java.util.List;

@Slf4j
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class LinkRepositoryTest {

    @InjectMocks
    private TestDataRepository testDataRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LinkRepository linkRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    public void findTop10ByUserAndLinkStateOrderByCreatedAtAsc_Success() throws Exception {
        // given
        User user = testDataRepository.createUser();
        userRepository.save(user);
        Category category = testDataRepository.createCategory();
        user.addCategories(Arrays.asList(category));
        categoryRepository.save(category);
        Link link = testDataRepository.createLink(category, user);
        linkRepository.save(link);

        List<Link> targetLinks = Arrays.asList(link);

        // when
        List<Link> actualLinks = linkRepository.findTop10ByUserAndLinkStateOrderByCreatedAtAsc(user, false);

        // then
        Assertions.assertThat(actualLinks.get(0).getTitle()).isEqualTo(targetLinks.get(0).getTitle());
    }
}