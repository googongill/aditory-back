package com.googongill.aditory.repository;

import com.googongill.aditory.domain.Link;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LinkRepository extends JpaRepository<Link, Long> {
}
