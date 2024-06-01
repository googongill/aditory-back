package com.googongill.aditory.repository;

import com.googongill.aditory.domain.Category;
import com.googongill.aditory.domain.User;
import com.googongill.aditory.domain.enums.CategoryState;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    @EntityGraph(attributePaths = "links")
    Optional<Category> findById(Long id);

    Optional<Category> findByCategoryName(String categoryName);

    List<Category> findAllByCategoryState(CategoryState categoryState);

    List<Category> findByAsCategoryNameContaining(String categoryName);

    Optional<Category> findByCategoryNameAndUser(String categoryName, User user);

}
