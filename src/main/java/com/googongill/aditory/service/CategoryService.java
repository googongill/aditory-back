package com.googongill.aditory.service;

import com.googongill.aditory.controller.dto.category.request.MoveCategoryRequest;
import com.googongill.aditory.controller.dto.category.request.UpdateCategoryRequest;
import com.googongill.aditory.domain.Category;
import com.googongill.aditory.domain.Link;
import com.googongill.aditory.domain.User;
import com.googongill.aditory.domain.enums.CategoryState;
import com.googongill.aditory.exception.CategoryException;
import com.googongill.aditory.exception.LinkException;
import com.googongill.aditory.exception.UserException;
import com.googongill.aditory.repository.CategoryRepository;
import com.googongill.aditory.repository.LinkRepository;
import com.googongill.aditory.repository.UserRepository;
import com.googongill.aditory.service.dto.category.*;
import com.googongill.aditory.service.dto.link.LinkInfo;

import com.googongill.aditory.controller.dto.category.request.CreateCategoryRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.googongill.aditory.common.code.CategoryErrorCode.*;
import static com.googongill.aditory.common.code.LinkErrorCode.LINK_NOT_FOUND;
import static com.googongill.aditory.common.code.LinkErrorCode.LINK_NOT_IN_CATEGORY;
import static com.googongill.aditory.common.code.UserErrorCode.USER_NOT_FOUND;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CategoryService {

    private final UserRepository userRepository;
    private final LinkRepository linkRepository;
    private final CategoryRepository categoryRepository;

    public CreateCategoryResult createCategory(CreateCategoryRequest createCategoryRequest, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(USER_NOT_FOUND));
        if (user.getCategories().size() >= 30) {
            throw new CategoryException(CATEGORY_LIMIT_EXCEEDED);
        }
        if (categoryRepository.findByCategoryNameAndUser(createCategoryRequest.getCategoryName(), user).isPresent()) {
            throw new CategoryException(CATEGORY_ALREADY_EXISTED);
        }
        Category createdCategory = categoryRepository.save(createCategoryRequest.toEntity(user));
        user.addCategory(createdCategory);
        return CreateCategoryResult.of(createdCategory);
    }

    public CopyCategoryResult copyCategory(Long categoryId, Long userId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryException(CATEGORY_NOT_FOUND));
        if (category.getCategoryState().equals(CategoryState.PRIVATE) && !category.getUser().getId().equals(userId)) {
            throw new CategoryException(CATEGORY_FORBIDDEN);
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(USER_NOT_FOUND));
        if (user.getCategories().stream().anyMatch(userCategory -> userCategory.getId().equals(categoryId))) {
            throw new CategoryException(CATEGORY_ALREADY_OWNED);
        }
        if (user.getCategories().size() >= 30) {
            throw new CategoryException(CATEGORY_LIMIT_EXCEEDED);
        }
        if (categoryRepository.findByCategoryNameAndUser(category.getAsCategoryName(), user).isPresent()) {
            throw new CategoryException(CATEGORY_ALREADY_EXISTED);
        }
        Category newCategory = new Category(category.getAsCategoryName(), category.getAsCategoryName(), user);
        List<Link> originalLinks = category.getLinks();
        List<Link> newLinks = originalLinks.stream()
                .map(link -> new Link(link.getTitle(), link.getSummary(), link.getUrl(), newCategory, user))
                .collect(Collectors.toList());
        newLinks.forEach(linkRepository::save);
        newCategory.getLinks().addAll(newLinks);
        user.getCategories().add(newCategory);
        categoryRepository.save(newCategory);

        return CopyCategoryResult.of(newCategory);
    }

    public CategoryDetailResult moveCategory(Long categoryId, MoveCategoryRequest moveCategoryRequest, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(USER_NOT_FOUND));

        Category originalCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryException(CATEGORY_NOT_FOUND));
        if (originalCategory.getCategoryState().equals(CategoryState.PRIVATE) && !originalCategory.getUser().getId().equals(userId)) {
            throw new CategoryException(CATEGORY_FORBIDDEN);
        }
        Category targetCategory = categoryRepository.findById(moveCategoryRequest.getTargetCategoryId())
                .orElseThrow(() -> new CategoryException(CATEGORY_NOT_FOUND));
        if (targetCategory.getCategoryState().equals(CategoryState.PRIVATE) && !targetCategory.getUser().getId().equals(userId)) {
            throw new CategoryException(CATEGORY_FORBIDDEN);
        }

        moveCategoryRequest.getLinkIdList().stream()
                .map(linkId -> {
                    Link link = linkRepository.findById(linkId)
                            .orElseThrow(() -> new LinkException(LINK_NOT_FOUND));
                    if (!link.getCategory().getId().equals(originalCategory.getId())) {
                        throw new LinkException(LINK_NOT_IN_CATEGORY);
                    }
                    link.setCategory(targetCategory);
                    targetCategory.getLinks().add(link);
                    return linkRepository.save(link);
                })
                .collect(Collectors.toList());

        List<LinkInfo> linkInfoList = targetCategory.getLinks().stream()
                .map(link -> LinkInfo.builder()
                        .linkId(link.getId())
                        .title(link.getTitle())
                        .summary(link.getSummary())
                        .linkState(link.getLinkState())
                        .createdAt(link.getCreatedAt())
                        .lastModifiedAt(link.getLastModifiedAt())
                        .build()
                ).collect(Collectors.toList());
        return CategoryDetailResult.of(targetCategory, linkInfoList);
    }

    public CategoryDetailResult getCategoryDetail(Long categoryId, Long userId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryException(CATEGORY_NOT_FOUND));
        if (category.getCategoryState().equals(CategoryState.PRIVATE) && !category.getUser().getId().equals(userId)) {
            throw new CategoryException(CATEGORY_FORBIDDEN);
        }
        List<LinkInfo> linkInfoList = category.getLinks().stream()
                .map(link -> LinkInfo.builder()
                        .linkId(link.getId())
                        .title(link.getTitle())
                        .summary(link.getSummary())
                        .url(link.getUrl())
                        .linkState(link.getLinkState())
                        .createdAt(link.getCreatedAt())
                        .lastModifiedAt(link.getLastModifiedAt())
                        .build()
                ).collect(Collectors.toList());
        return CategoryDetailResult.of(category, linkInfoList);
    }

    public CategoryListResult getMyCategoryList(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(USER_NOT_FOUND));
        List<CategoryInfo> myCategoryInfoList = user.getCategories().stream()
                .map(category -> CategoryInfo.builder()
                        .categoryId(category.getId())
                        .categoryName(category.getCategoryName())
                        .asCategoryName(category.getAsCategoryName())
                        .linkCount(category.getLinks().size())
                        .likeCount(category.getCategoryLikes().size())
                        .categoryState(category.getCategoryState())
                        .prevLinks(category.getLinks().stream()
                                .sorted(Comparator.comparing(Link::getCreatedAt).reversed())
                                .limit(4)
                                .map(Link::getUrl)
                                .collect(Collectors.toList())
                        )
                        .createdAt(category.getCreatedAt())
                        .lastModifiedAt(category.getLastModifiedAt())
                        .build())
                .collect(Collectors.toList());
        return CategoryListResult.of(myCategoryInfoList);
    }

    public Page<CategoryInfo> getPublicCategoryList(Pageable pageable, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(USER_NOT_FOUND));
        PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("lastModifiedAt").descending());
        Page<Category> categories = categoryRepository.findAllByCategoryState(CategoryState.PUBLIC, pageRequest);
        return categories.map(category -> CategoryInfo.builder()
                .categoryId(category.getId())
                .categoryName(category.getCategoryName())
                .asCategoryName(category.getAsCategoryName())
                .linkCount(category.getLinks().size())
                .likeCount(category.getCategoryLikes().size())
                .categoryState(category.getCategoryState())
                .prevLinks(category.getLinks().stream()
                        .sorted(Comparator.comparing(Link::getCreatedAt).reversed())
                        .limit(4)
                        .map(Link::getUrl)
                        .collect(Collectors.toList())
                )
                .createdAt(category.getCreatedAt())
                .lastModifiedAt(category.getLastModifiedAt())
                .build());
    }

    public CategoryListResult getTodayPublicCategoryList(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(USER_NOT_FOUND));
        List<CategoryInfo> categoryInfos = categoryRepository.findRandomByCategoryState(CategoryState.PUBLIC.toString()).stream()
                .map(category -> CategoryInfo.builder()
                        .categoryId(category.getId())
                        .categoryName(category.getCategoryName())
                        .asCategoryName(category.getAsCategoryName())
                        .linkCount(category.getLinks().size())
                        .likeCount(category.getCategoryLikes().size())
                        .categoryState(category.getCategoryState())
                        .prevLinks(category.getLinks().stream()
                                .sorted(Comparator.comparing(Link::getCreatedAt).reversed())
                                .limit(4)
                                .map(Link::getUrl)
                                .collect(Collectors.toList())
                        )
                        .createdAt(category.getCreatedAt())
                        .lastModifiedAt(category.getLastModifiedAt())
                        .build()
                )
                .collect(Collectors.toList());

        return CategoryListResult.of(categoryInfos);
    }

    public UpdateCategoryResult updateCategory(Long categoryId, UpdateCategoryRequest updateCategoryRequest, Long userId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryException(CATEGORY_NOT_FOUND));
        if (category.getCategoryState().equals(CategoryState.PRIVATE) && !category.getUser().getId().equals(userId)) {
            throw new CategoryException(CATEGORY_FORBIDDEN);
        }
        category.updateCategoryInfo(updateCategoryRequest.getCategoryName(),updateCategoryRequest.getAsCategoryName(),updateCategoryRequest.getCategoryState());
        categoryRepository.save(category);
        return UpdateCategoryResult.of(category);
    }

    public List<Category> importCategories(MultipartFile importFile, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(USER_NOT_FOUND));

        List<Category> newCategories = new ArrayList<>();

        try {
            Document doc = Jsoup.parse(importFile.getInputStream(), "UTF-8", "");

            Elements unReadElements = doc.select("h1:contains(Unread) + ul > li > a");
            processElements(unReadElements, user, true, newCategories);

            Elements readElements = doc.select("h1:contains(Read Archive) + ul > li > a");
            processElements(readElements, user, true, newCategories);

        } catch (IOException e) {
            throw new CategoryException(IMPORT_FILE_PARSE_FAIL);
        }

        return newCategories;
    }

    private void processElements(Elements elements, User user, boolean linkState, List<Category> newCategories) {
        elements.stream()
                .map(element -> {
                    String categoryName = element.attr("tags");
                    String linkTitle = element.text();
                    String url = element.attr("href");

                    if (!categoryName.isEmpty()) {
                        addLinkToAlreadyExistingCategory(user, linkState, categoryName, linkTitle, url, newCategories);
                    } else {
                        addLinkAndCategory(user, linkState, linkTitle, url, newCategories);
                    }
                    return element;
                })
                .collect(Collectors.toList());
    }

    private void addLinkToAlreadyExistingCategory(User user, boolean linkState, String categoryName, String linkTitle, String url, List<Category> newCategories) {
        Category category = categoryRepository.findByCategoryNameAndUser(categoryName, user)
                .orElseGet(() -> {
                    Category newCategory = categoryRepository.save(new Category(categoryName, categoryName, user));
                    user.addCategory(newCategory);
                    newCategories.add(newCategory);
                    return newCategory;
                });
        Link link = new Link(linkTitle, url, linkState, category, user);
        linkRepository.save(link);
        category.addLink(link);
        user.addLink(link);
    }

    private void addLinkAndCategory(User user, boolean linkState, String linkTitle, String url, List<Category> newCategories) {
        Category category = categoryRepository.findByCategoryName("imported Category")
                .orElseGet(() -> {
                    Category newCategory = categoryRepository.save(new Category("imported Category", "imported Category", user));
                    user.addCategory(newCategory);
                    newCategories.add(newCategory);
                    return newCategory;
                });
        Link link = new Link(linkTitle, url, linkState, category, user);
        linkRepository.save(link);
        category.addLink(link);
        user.addLink(link);
    }
}