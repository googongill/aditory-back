package com.googongill.aditory.repository;

import com.googongill.aditory.domain.Category;
import com.googongill.aditory.domain.CategoryLike;
import com.googongill.aditory.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryLikeRepository extends JpaRepository<CategoryLike, Long> {

    boolean existsByUserAndCategory(User user, Category category);

    Optional<CategoryLike> findByUserAndCategory(User user, Category category);

    List<CategoryLike> findByUser(User user);
}