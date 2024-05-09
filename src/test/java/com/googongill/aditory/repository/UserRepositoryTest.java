package com.googongill.aditory.repository;

import com.googongill.aditory.TestDataRepository;
import com.googongill.aditory.TestUtils;
import com.googongill.aditory.domain.Category;
import com.googongill.aditory.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@Slf4j
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    @InjectMocks
    private TestDataRepository testDataRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TestEntityManager testEntityManager;

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