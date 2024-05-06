package com.googongill.aditory.repository;

import com.googongill.aditory.domain.User;
import com.googongill.aditory.domain.enums.Role;
import com.googongill.aditory.domain.enums.SocialType;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

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
    public void findByUsername_Success() {
        // given
        User user = new User("testUser",
                "testPw",
                Role.ROLE_USER, SocialType.LOCAL,
                "테스트 유저",
                "010-1234-5678");

        // when
        Optional<User> findUser = userRepository.findByUsername(user.getUsername());

        // then
        assertThat(findUser).isNotEmpty();
        assertThat(findUser.get()).isEqualTo(user);
    }
}