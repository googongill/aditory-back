package com.googongill.aditory.service;

import com.googongill.aditory.controller.dto.category.MoveCategoryRequest;
import com.googongill.aditory.controller.dto.category.UpdateCategoryRequest;
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

import com.googongill.aditory.controller.dto.category.CreateCategoryRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
        // user 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(USER_NOT_FOUND));
        // category 갯수 제한
        if (user.getCategories().size() >= 30) {
            throw new CategoryException(CATEGORY_LIMIT_EXCEEDED);
        }
        // category 생성
        Category createdCategory = categoryRepository.save(createCategoryRequest.toEntity(user));
        user.addCategory(createdCategory);
        return CreateCategoryResult.of(createdCategory);
    }

    //카테고리 복사
    public CopyCategoryResult copyCategory(Long categoryId, Long userId) {
        // 카테고리 조회
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryException(CATEGORY_NOT_FOUND));
        // category 의 state 가 private 인데 카테고리의 소유주가 아닌 user 가 접근하는 경우
        if (category.getCategoryState().equals(CategoryState.PRIVATE) && !category.getUser().getId().equals(userId)) {
            throw new CategoryException(CATEGORY_FORBIDDEN);
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(USER_NOT_FOUND));
        if (user.getCategories().stream().anyMatch(userCategory -> userCategory.getId().equals(categoryId))) {
            throw new CategoryException(CATEGORY_ALREADY_OWNED);
        }
        // category 갯수 제한
        if (user.getCategories().size() >= 30) {
            throw new CategoryException(CATEGORY_LIMIT_EXCEEDED);
        }
        // 새카테고리 생성 및 user 설정
        Category newCategory = new Category(category.getAsCategoryName(), category.getAsCategoryName(), user);
        // 원본 카테고리의 링크를 가져옴
        List<Link> originalLinks = category.getLinks();
        // 링크를 복사하여 새로운 카테고리에 추가
        List<Link> newLinks = originalLinks.stream()
                .map(link -> new Link(link.getTitle(), link.getSummary(), link.getUrl(), newCategory, user))
                .collect(Collectors.toList());
        newLinks.forEach(linkRepository::save);
        newCategory.getLinks().addAll(newLinks);
        // 새카테고리를 사용자의 카테고리 목록에 추가
        user.getCategories().add(newCategory);
        // 새 카테고리 저장
        categoryRepository.save(newCategory);

        return CopyCategoryResult.of(newCategory);
    }

    // 카테고리 속 링크 이동
    public CategoryDetailResult moveCategory(Long categoryId, MoveCategoryRequest moveCategoryRequest, Long userId) {
        // user 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(USER_NOT_FOUND));

        // 원래 카테고리 조회
        Category originalCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryException(CATEGORY_NOT_FOUND));
        // 원래 카테고리의 state 가 private 인데 카테고리의 소유주가 아닌 user 가 접근하는 경우
        if (originalCategory.getCategoryState().equals(CategoryState.PRIVATE) && !originalCategory.getUser().getId().equals(userId)) {
            throw new CategoryException(CATEGORY_FORBIDDEN);
        }
        // 대상 카테고리 조회
        Category targetCategory = categoryRepository.findById(moveCategoryRequest.getTargetCategoryId())
                .orElseThrow(() -> new CategoryException(CATEGORY_NOT_FOUND));
        // 대상 카테고리의 state 가 private 인데 카테고리의 소유주가 아닌 user 가 접근하는 경우
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

        // 대상 카테고리의 링크 목록 조회하며 각 링크별 정보 입력
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
        // 카테고리 조회
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryException(CATEGORY_NOT_FOUND));
        // category 의 state 가 private 인데 카테고리의 소유주가 아닌 user 가 접근하는 경우
        if (category.getCategoryState().equals(CategoryState.PRIVATE) && !category.getUser().getId().equals(userId)) {
            throw new CategoryException(CATEGORY_FORBIDDEN);
        }
        // 조회한 category 의 링크 목록 조회하며 각 링크별 정보 입력
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
        // user 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(USER_NOT_FOUND));
        // 조회한 user 의 카테고리 목록 조회하며 각 카테고리별 정보 입력
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

    public CategoryListResult getPublicCategoryList(Long userId) {
        // user 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(USER_NOT_FOUND));
        // 모든 사용자 카테고리 중에서 state가 public 인 것만 조회
        List<CategoryInfo> categoryInfoList = categoryRepository.findAllByCategoryState(CategoryState.PUBLIC).stream()
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
        return CategoryListResult.of(categoryInfoList);
    }

    public UpdateCategoryResult updateCategory(Long categoryId, UpdateCategoryRequest updateCategoryRequest, Long userId) {
        // 카테고리 조회
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryException(CATEGORY_NOT_FOUND));
        // category 의 state 가 private 인데 카테고리의 소유주가 아닌 user 가 접근하는 경우
        if (category.getCategoryState().equals(CategoryState.PRIVATE) && !category.getUser().getId().equals(userId)) {
            throw new CategoryException(CATEGORY_FORBIDDEN);
        }
        category.updateCategoryInfo(updateCategoryRequest.getCategoryName(),updateCategoryRequest.getAsCategoryName(),updateCategoryRequest.getCategoryState());
        categoryRepository.save(category);
        return UpdateCategoryResult.of(category);
    }

    public void importCategories(MultipartFile importFile, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(USER_NOT_FOUND));
        try {
            Document doc = Jsoup.parse(importFile.getInputStream(), "UTF-8", "");

            Elements unReadElements = doc.select("h1:contains(Unread) + ul > li > a");
            processElements(unReadElements, user, true);

            Elements readElements = doc.select("h1:contains(Read Archive) + ul > li > a");
            processElements(readElements, user, true);

        } catch (IOException e) {
            throw new CategoryException(IMPORT_FILE_PARSE_FAIL);
        }
    }

    private void processElements(Elements elements, User user, boolean linkState) {
        elements.stream()
                .map(element -> {
                    String categoryName = element.attr("tags");
                    String linkTitle = element.text();
                    String url = element.attr("href");

                    if (!categoryName.isEmpty()) {
                        addLinkToAlreadyExistingCategory(user, linkState, categoryName, linkTitle, url);
                    } else {
                        addLinkAndCategory(user, linkState, linkTitle, url);
                    }
                    return element;
                })
                .collect(Collectors.toList());
    }

    private void addLinkToAlreadyExistingCategory(User user, boolean linkState, String categoryName, String linkTitle, String url) {
        Category category = categoryRepository.findByCategoryNameAndUser(categoryName, user)
                .orElseGet(() -> {
                    Category newCategory = categoryRepository.save(new Category(categoryName, "(default)", user));
                    user.addCategory(newCategory);
                    return newCategory;
                });
        Link link = new Link(linkTitle, url, linkState, category, user);
        linkRepository.save(link);
        category.addLink(link);
        user.addLink(link);
    }

    private void addLinkAndCategory(User user, boolean linkState, String linkTitle, String url) {
        Category category = categoryRepository.findByCategoryName("imported Category")
                .orElseGet(() -> {
                    Category newCategory = categoryRepository.save(new Category("imported Category", "(default)", user));
                    user.addCategory(newCategory);
                    return newCategory;
                });
        Link link = new Link(linkTitle, url, linkState, category, user);
        linkRepository.save(link);
        category.addLink(link);
        user.addLink(link);
    }
}