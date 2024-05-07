package com.googongill.aditory.repository;

import com.googongill.aditory.TestUtils;
import com.googongill.aditory.domain.Category;
import com.googongill.aditory.domain.User;
import com.googongill.aditory.domain.enums.Role;
import com.googongill.aditory.domain.enums.SocialType;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Arrays;
import java.util.Optional;

import static com.googongill.aditory.TestDataRepository.createCategory;
import static com.googongill.aditory.TestDataRepository.createUser;
import static org.assertj.core.api.Assertions.*;

@Slf4j
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    public void findById_Success() throws Exception {
        // given
        Long userId = 1L;
        User user = createUser();
        TestUtils.setEntityId(userId, user);
        Category category = createCategory();
        user.addCategories(Arrays.asList(category));
        userRepository.save(user);

        // when
        Optional<User> foundUser = userRepository.findById(userId);

        // then
        assertThat(foundUser).isNotEmpty();
        assertThat(foundUser.get().getId()).isEqualTo(userId);
        assertThat(foundUser.get().getCategories().get(0).getCategoryName()).isEqualTo(category.getCategoryName());
    }

    @Test
    public void findById_NotExists_ReturnsEmptyOptional() {
        // given
        Long nonExistentUserId = 999L; // 존재하지 않는 ID

        // when
        Optional<User> foundUser = userRepository.findById(nonExistentUserId);

        // then
        assertThat(foundUser).isEmpty();
    }
}