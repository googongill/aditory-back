package com.googongill.aditory.repository;

import com.googongill.aditory.domain.Category;
import com.googongill.aditory.domain.User;
import com.googongill.aditory.domain.enums.CategoryState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    @EntityGraph(attributePaths = "links")
    Optional<Category> findById(Long id);

    Optional<Category> findByCategoryName(String categoryName);

    @Query(value = "select c from Category c where c.categoryState = :categoryState")
    Page<Category> findAllByCategoryState(@Param("categoryState") CategoryState categoryState, Pageable pageable);

    @Query(value = "select * from category where category_state = :categoryState order by rand() limit 10", nativeQuery = true)
    List<Category> findRandomByCategoryState(@Param("categoryState") String categoryState);

    Optional<Category> findByCategoryNameAndUser(String categoryName, User user);

    Page<Category> findByCategoryNameContainingAndUser(String query, User user, Pageable pageable);

    Page<Category> findByAsCategoryNameContainingAndCategoryState(String query, CategoryState categoryState, Pageable pageable);
}
