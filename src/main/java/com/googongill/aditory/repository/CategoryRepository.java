package com.googongill.aditory.repository;

import com.googongill.aditory.domain.Category;
import com.googongill.aditory.domain.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    @EntityGraph(attributePaths = "links")
    Optional<Category> findById(Long id);

    Optional<Category> findByCategoryName(String categoryName);
}
