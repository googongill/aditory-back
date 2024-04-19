package com.googongill.aditory.repository;

import com.googongill.aditory.domain.User;
import com.googongill.aditory.domain.enums.Role;
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
    private TestEntityManager em;

    @Test
    public void findByUsername_Success() {
        // given
        User user = new User("testUser", "1234", Role.ROLE_USER, "testNickname", "010-1234-5678");
        userRepository.save(user);

        // when
        Optional<User> findUser = userRepository.findByUsername(user.getUsername());

        // then
        assertThat(findUser).isNotEmpty();
        assertThat(findUser.get()).isEqualTo(user);
    }
}