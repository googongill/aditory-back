package com.googongill.aditory.repository;

import com.googongill.aditory.domain.Link;
import com.googongill.aditory.domain.User;
import com.googongill.aditory.domain.enums.CategoryState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LinkRepository extends JpaRepository<Link, Long> {
    List<Link> findTop10ByUserAndLinkStateOrderByCreatedAtAsc(User user, boolean linkState);

    Page<Link> findByTitleContainingAndUser(String query, User user, Pageable pageable);

    Page<Link> findByTitleContainingAndCategory_CategoryState(String query, CategoryState categoryState, Pageable pageable);
}
