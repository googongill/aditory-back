package com.googongill.aditory.repository;

import com.googongill.aditory.domain.Link;
import com.googongill.aditory.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LinkRepository extends JpaRepository<Link, Long> {
    List<Link> findTop10ByUserAndLinkStateOrderByCreatedAtAsc(User user, boolean linkState);

    List<Link> findByTitleContaining(String title);
}
