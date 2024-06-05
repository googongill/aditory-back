package com.googongill.aditory.repository;

import com.googongill.aditory.domain.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @EntityGraph(attributePaths = {"categories", "categories.links", "links"})
    Optional<User> findById(Long id);

    Optional<User> findByUsername(String username);

    Optional<User> findBySocialId(String socialId);
}
