package com.googongill.aditory.service;

import com.googongill.aditory.TestDataRepository;
import com.googongill.aditory.TestUtils;
import com.googongill.aditory.controller.dto.link.CreateLinkRequest;
import com.googongill.aditory.controller.dto.link.UpdateLinkRequest;
import com.googongill.aditory.domain.Category;
import com.googongill.aditory.domain.Link;
import com.googongill.aditory.domain.User;
import com.googongill.aditory.exception.CategoryException;
import com.googongill.aditory.exception.LinkException;
import com.googongill.aditory.exception.UserException;
import com.googongill.aditory.external.chatgpt.ChatGptService;
import com.googongill.aditory.external.chatgpt.dto.AutoCategorizeResult;
import com.googongill.aditory.repository.CategoryRepository;
import com.googongill.aditory.repository.LinkRepository;
import com.googongill.aditory.repository.UserRepository;
import com.googongill.aditory.service.dto.link.LinkResult;
import com.googongill.aditory.service.dto.link.ReminderResult;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static com.googongill.aditory.common.code.CategoryErrorCode.CATEGORY_FORBIDDEN;
import static com.googongill.aditory.common.code.CategoryErrorCode.CATEGORY_NOT_FOUND;
import static com.googongill.aditory.common.code.LinkErrorCode.LINK_NOT_FOUND;
import static com.googongill.aditory.common.code.LinkErrorCode.REMINDER_EMPTY;
import static com.googongill.aditory.common.code.UserErrorCode.USER_NOT_FOUND;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@Slf4j
@ExtendWith(MockitoExtension.class)
class LinkServiceTest {

    @InjectMocks
    private LinkService linkService;
    @InjectMocks
    private TestDataRepository testDataRepository;
    @Mock
    private ChatGptService chatGptService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private LinkRepository linkRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private User user;
    @Mock
    private Category category;

    @BeforeEach
    public void init() throws Exception {
        user = testDataRepository.createUser();
        Long userId = 0L;
        TestUtils.setEntityId(userId, user);

        List<Category> categoryList = testDataRepository.createCategories();
        category = categoryList.get(0);
        Long categoryId = 0L;
        TestUtils.setEntityId(categoryId, category);

        user.addCategories(categoryList);
    }

    @Test
    public void getCreateLinkResult_Success() throws Exception {
        // given
        CreateLinkRequest createLinkRequest = testDataRepository.createCreateLinkRequest();
        LinkResult targetResult = testDataRepository.createLinkResult();

        Link link = createLinkRequest.toEntity(category, user);
        Long linkId = 0L;
        TestUtils.setEntityId(linkId, link);

        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(categoryRepository.findById(createLinkRequest.getCategoryId())).willReturn(Optional.of(category));
        given(linkRepository.save(any(Link.class))).willReturn(link);

        // when
        LinkResult actualResult = linkService.createLink(createLinkRequest, user.getId());

        // then
        Assertions.assertThat(actualResult).isNotNull();
        Assertions.assertThat(actualResult.getLinkId()).isEqualTo(targetResult.getLinkId());
    }

    @Test
    public void getCreateLinkResult_Failed_With_NotExistingUser() throws Exception {
        // given
        CreateLinkRequest createLinkRequest = testDataRepository.createCreateLinkRequest();

        given(userRepository.findById(anyLong())).willReturn(Optional.empty());

        // when
        UserException exception = org.junit.jupiter.api.Assertions.assertThrows(UserException.class, () -> linkService.createLink(createLinkRequest, user.getId()));

        // then
        org.junit.jupiter.api.Assertions.assertEquals(USER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    public void getCreateLinkResult_Failed_With_NotExistingCategory() throws Exception {
        // given
        CreateLinkRequest createLinkRequest = testDataRepository.createCreateLinkRequest();

        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(categoryRepository.findById(anyLong())).willReturn(Optional.empty());

        // when
        CategoryException exception = org.junit.jupiter.api.Assertions.assertThrows(CategoryException.class, () -> linkService.createLink(createLinkRequest, user.getId()));

        // then
        org.junit.jupiter.api.Assertions.assertEquals(CATEGORY_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    public void getCreateLinkResult_Failed_With_ForbiddenCategory() throws Exception {
        // given
        User anotherUser = testDataRepository.createUser();
        Long anotherUserId = 123L;
        TestUtils.setEntityId(anotherUserId, anotherUser);

        CreateLinkRequest createLinkRequest = testDataRepository.createCreateLinkRequest();

        given(userRepository.findById(anotherUser.getId())).willReturn(Optional.of(anotherUser));
        given(categoryRepository.findById(createLinkRequest.getCategoryId())).willReturn(Optional.of(category));

        // when
        CategoryException exception = org.junit.jupiter.api.Assertions.assertThrows(CategoryException.class, () -> linkService.createLink(createLinkRequest, anotherUser.getId()));

        // then
        org.junit.jupiter.api.Assertions.assertEquals(CATEGORY_FORBIDDEN, exception.getErrorCode());
    }

    @Test
    public void getAutoCreateLinkResult_Success() throws Exception {
        // given
        CreateLinkRequest createLinkRequest = testDataRepository.createAutoCreateLinkRequest();
        LinkResult targetResult = testDataRepository.createLinkResult();
        AutoCategorizeResult autoCategorizeResult = testDataRepository.createAuthCategorizeResult();

        Link link = createLinkRequest.toEntity(category, user);
        Long linkId = 0L;
        TestUtils.setEntityId(linkId, link);

        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(chatGptService.autoCategorizeLink(any(), any())).willReturn(autoCategorizeResult);
        given(categoryRepository.findByCategoryName(autoCategorizeResult.getCategoryName())).willReturn(Optional.of(category));
        given(linkRepository.save(any(Link.class))).willReturn(link);

        // when
        LinkResult actualResult = linkService.createLink(createLinkRequest, user.getId());

        // then
        Assertions.assertThat(actualResult).isNotNull();
        Assertions.assertThat(actualResult.getLinkId()).isEqualTo(targetResult.getLinkId());
    }

    @Test
    public void getAutoCreateLinkResult_Failed_With_NotExistingUser() throws Exception {
        // given
        CreateLinkRequest createLinkRequest = testDataRepository.createAutoCreateLinkRequest();

        given(userRepository.findById(anyLong())).willReturn(Optional.empty());

        // when
        UserException exception = org.junit.jupiter.api.Assertions.assertThrows(UserException.class, () -> linkService.createLink(createLinkRequest, user.getId()));

        // then
        org.junit.jupiter.api.Assertions.assertEquals(USER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    public void getAutoCreateLinkResult_Failed_With_NotExistingCategory() throws Exception {
        // given
        CreateLinkRequest createLinkRequest = testDataRepository.createAutoCreateLinkRequest();
        AutoCategorizeResult autoCategorizeResult = testDataRepository.createAuthCategorizeResult();

        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(chatGptService.autoCategorizeLink(any(), any())).willReturn(autoCategorizeResult);
        given(categoryRepository.findByCategoryName(any())).willReturn(Optional.empty());

        // when
        CategoryException exception = org.junit.jupiter.api.Assertions.assertThrows(CategoryException.class, () -> linkService.createLink(createLinkRequest, user.getId()));

        // then
        org.junit.jupiter.api.Assertions.assertEquals(CATEGORY_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    public void updateLink_Success() throws Exception {
        // given
        UpdateLinkRequest updateLinkRequest = testDataRepository.createUpdateLinkRequest();
        LinkResult targetResult = testDataRepository.createLinkResult();

        Link link = testDataRepository.createLink(category, user);
        Long linkId = 0L;
        TestUtils.setEntityId(linkId, link);

        given(linkRepository.findById(linkId)).willReturn(Optional.of(link));
        given(categoryRepository.findById(category.getId())).willReturn(Optional.of(category));

        // when
        LinkResult actualResult = linkService.updateLink(linkId, updateLinkRequest, user.getId());

        // then
        Assertions.assertThat(actualResult.getLinkId()).isEqualTo(targetResult.getLinkId());
        Assertions.assertThat(actualResult.getCategoryId()).isEqualTo(targetResult.getCategoryId());
    }

    @Test
    public void updateLink_Failed_With_NotExistingLink() throws Exception {
        // given
        UpdateLinkRequest updateLinkRequest = testDataRepository.createUpdateLinkRequest();

        Long linkId = -1L;

        given(linkRepository.findById(anyLong())).willReturn(Optional.empty());

        // when
        LinkException exception = org.junit.jupiter.api.Assertions.assertThrows(LinkException.class, () -> linkService.updateLink(linkId, updateLinkRequest, user.getId()));

        // then
        org.junit.jupiter.api.Assertions.assertEquals(LINK_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    public void updateLink_Failed_With_NotExistingCategory() throws Exception {
        // given
        UpdateLinkRequest updateLinkRequest = testDataRepository.createUpdateLinkRequest();

        Link link = testDataRepository.createLink(category, user);
        Long linkId = 0L;
        TestUtils.setEntityId(linkId, link);

        given(linkRepository.findById(linkId)).willReturn(Optional.of(link));
        given(categoryRepository.findById(anyLong())).willReturn(Optional.empty());

        // when
        CategoryException exception = org.junit.jupiter.api.Assertions.assertThrows(CategoryException.class, () -> linkService.updateLink(linkId, updateLinkRequest, user.getId()));

        // then
        org.junit.jupiter.api.Assertions.assertEquals(CATEGORY_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    public void getReminder_Success() throws Exception {
        // given
        ReminderResult targetResult = testDataRepository.createReminderResult();

        Link link = testDataRepository.createLink(category, user);
        Long linkId = 0L;
        TestUtils.setEntityId(linkId, link);

        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(linkRepository.findTop10ByUserAndLinkStateOrderByCreatedAtAsc(user, false)).willReturn(Arrays.asList(link));

        // when
        ReminderResult actualResult = linkService.getReminder(user.getId());

        // then
        Assertions.assertThat(actualResult.getLinkList().get(0).getTitle()).isEqualTo(targetResult.getLinkList().get(0).getTitle());
    }

    @Test
    public void getReminder_Failed_With_NotExistingUser() throws Exception {
        // given
        Long userId = -1L;

        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when
        UserException exception = org.junit.jupiter.api.Assertions.assertThrows(UserException.class, () -> linkService.getReminder(userId));

        // then
        org.junit.jupiter.api.Assertions.assertEquals(USER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    public void getReminder_Failed_With_ReturnEmptyList() throws Exception {
        // given
        ReminderResult targetResult = testDataRepository.createReminderResult();

        Link link = testDataRepository.createLink(category, user);
        Long linkId = 0L;
        TestUtils.setEntityId(linkId, link);

        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(linkRepository.findTop10ByUserAndLinkStateOrderByCreatedAtAsc(user, false)).willReturn(Collections.EMPTY_LIST);

        // when
        LinkException exception = org.junit.jupiter.api.Assertions.assertThrows(LinkException.class, () -> linkService.getReminder(user.getId()));

        // then
        org.junit.jupiter.api.Assertions.assertEquals(REMINDER_EMPTY, exception.getErrorCode());
    }
}
