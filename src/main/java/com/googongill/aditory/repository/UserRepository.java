package com.googongill.aditory.repository;

import com.googongill.aditory.domain.User;
import com.googongill.aditory.domain.enums.SocialType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @EntityGraph(attributePaths = {"categories", "links"})
    Optional<User> findById(Long id);

    Optional<User> findByUsername(String username);

    Optional<User> findBySocialTypeAndSocialId(SocialType socialType, String id);
}
