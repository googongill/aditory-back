package com.googongill.aditory.service;

import com.googongill.aditory.external.chatgpt.ChatGptService;
import com.googongill.aditory.repository.CategoryRepository;
import com.googongill.aditory.repository.LinkRepository;
import com.googongill.aditory.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class LinkServiceTest {

    @InjectMocks
    private LinkService linkService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private LinkRepository linkRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private ChatGptService chatGptService;

    @Test
    public void getCreateLinkResult_Success() throws Exception {
        // given

        // when

        // then

    }

    @Test
    public void getCreateLinkResult_Failed_With_NotExistingUser() throws Exception {
        // given

        // when

        // then

    }

    @Test
    public void getCreateLinkResult_Failed_With_NotExistingCategory() throws Exception {
        // given

        // when

        // then

    }

    @Test
    public void getCreateLinkResult_Failed_With_ForbiddenCategory() throws Exception {
        // given

        // when

        // then

    }

    @Test
    public void getAutoCreateLinkResult_Success() throws Exception {
        // given

        // when

        // then

    }

    @Test
    public void getAutoCreateLinkResult_Failed_With_NotExistingUser() throws Exception {
        // given

        // when

        // then

    }

    @Test
    public void getAutoCreateLinkResult_Failed_With_NotExistingCategory() throws Exception {
        // given

        // when

        // then

    }

    @Test
    public void updateLink_Success() throws Exception {
        // given

        // when

        // then

    }

    @Test
    public void updateLink_Failed_With_NotExistingLink() throws Exception {
        // given

        // when

        // then

    }

    @Test
    public void updateLink_Failed_With_NotExistingCategory() throws Exception {
        // given

        // when

        // then

    }

    @Test
    public void getReminder_Success() throws Exception {
        // given

        // when

        // then

    }

    @Test
    public void getReminder_Failed_With_NotExistingUser() throws Exception {
        // given

        // when

        // then

    }
}
