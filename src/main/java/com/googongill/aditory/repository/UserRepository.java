package com.googongill.aditory.repository;

import com.googongill.aditory.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
