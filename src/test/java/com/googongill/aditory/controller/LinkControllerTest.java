package com.googongill.aditory.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.googongill.aditory.TestDataRepository;
import com.googongill.aditory.TestUtils;
import com.googongill.aditory.controller.dto.link.CreateLinkRequest;
import com.googongill.aditory.controller.dto.link.UpdateLinkRequest;
import com.googongill.aditory.domain.Category;
import com.googongill.aditory.domain.Link;
import com.googongill.aditory.domain.User;
import com.googongill.aditory.exception.LinkException;
import com.googongill.aditory.repository.LinkRepository;
import com.googongill.aditory.security.jwt.TokenProvider;
import com.googongill.aditory.security.jwt.user.PrincipalDetails;
import com.googongill.aditory.security.jwt.user.PrincipalDetailsService;
import com.googongill.aditory.service.LinkService;
import com.googongill.aditory.service.dto.link.LinkResult;
import com.googongill.aditory.service.dto.link.LinkListResult;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Optional;

import static com.googongill.aditory.common.code.LinkErrorCode.LINK_FORBIDDEN;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@MockBean(JpaMetamodelMappingContext.class)
@WebMvcTest(controllers = LinkController.class)
@ComponentScan(basePackages = "com.googongill.aditory.security")
class LinkControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private LinkService linkService;
    @MockBean
    private LinkRepository linkRepository;
    @MockBean
    private TokenProvider tokenProvider;
    @MockBean
    private PrincipalDetails principalDetails;
    @MockBean
    private PrincipalDetailsService principalDetailsService;
    @InjectMocks
    private TestDataRepository testDataRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Mock
    private User user;
    private String accessToken = "";

    @BeforeEach
    public void init(@Value("${jwt.test-secret}") String TEST_SECRET) throws Exception {
        tokenProvider = new TokenProvider(TEST_SECRET);

        user = testDataRepository.createUser();
        TestUtils.setEntityId(0L, user);
        accessToken = tokenProvider.createTokens(user.getId(), user.getUsername(), user.getRole()).getAccessToken();
        given(principalDetailsService.loadUserByUsername(user.getUsername())).willReturn(new PrincipalDetails(user));
        given(principalDetails.getUserId()).willReturn(user.getId());
    }

    @Test
    public void createLink_Success() throws Exception {
        // given
        CreateLinkRequest createLinkRequest = testDataRepository.createCreateLinkRequest();
        LinkResult linkResult = testDataRepository.createLinkResult();
        String successCreateLinkRequestJson = objectMapper.writeValueAsString(createLinkRequest);

        given(linkService.createLink(createLinkRequest, principalDetails.getUserId())).willReturn(linkResult);

        // when
        ResultActions actions = mockMvc.perform(
                post("/links")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(successCreateLinkRequestJson)
                        .header("Authorization", "Bearer " + accessToken)
        );

        // then
        actions.andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.categoryId").value(linkResult.getCategoryId()));
    }

    @Test
    public void createLink_Failed_Without_RequiredField() throws Exception {
        // given
        CreateLinkRequest createLinkRequest = testDataRepository.createCreateLinkRequest();
        LinkResult linkResult = testDataRepository.createLinkResult();

        createLinkRequest.setUrl("");
        String failCreateLinkRequestJson = objectMapper.writeValueAsString(createLinkRequest);

        given(linkService.createLink(createLinkRequest, principalDetails.getUserId())).willReturn(linkResult);

        // when
        ResultActions actions = mockMvc.perform(
                post("/links")
                        .with(csrf())
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(failCreateLinkRequestJson)
        );

        // then
        MvcResult emptyUrlResult = actions.andExpect(status().isBadRequest()).andReturn();
        Assertions.assertThat(emptyUrlResult.getResolvedException()).isExactlyInstanceOf(MethodArgumentNotValidException.class);
    }

    @Test
    public void getLink_Success() throws Exception {
        // given
        Category category = testDataRepository.createCategory();

        Link link = testDataRepository.createLink(category, user);
        Long linkId = 0L;
        TestUtils.setEntityId(linkId, link);

        given(linkRepository.findById(link.getId())).willReturn(Optional.of(link));

        // when
        ResultActions actions = mockMvc.perform(
                get("/links/{linkId}", linkId)
                        .with(csrf())
                        .header("Authorization", "Bearer " + accessToken)
        );

        // then
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.linkId").value(linkId))
                .andExpect(jsonPath("$.data.title").value(link.getTitle()));
    }

    @Test
    public void getLink_Failed_With_NotExistingLink() throws Exception {
        // given
        Long notExistingLinkId = -1L;

        given(linkRepository.findById(notExistingLinkId)).willReturn(Optional.empty());

        // when
        ResultActions actions = mockMvc.perform(
                get("/links/{linkId}", notExistingLinkId)
                        .with(csrf())
                        .header("Authorization", "Bearer " + accessToken)
        );

        // then
        MvcResult notExistingLinkResult = actions.andExpect(status().isNotFound()).andReturn();
        Assertions.assertThat(notExistingLinkResult.getResolvedException()).isExactlyInstanceOf(LinkException.class);
    }

    @Test
    public void getLink_Failed_With_ForbiddenLink() throws Exception {
        // given
        User anotherUser = testDataRepository.createUser();
        TestUtils.setEntityId(123L, anotherUser);
        Category pricateCategory = testDataRepository.createCategory();
        pricateCategory.setUser(anotherUser);

        Link forbiddenLink = testDataRepository.createLink(pricateCategory, anotherUser);
        Long forbiddenLinkId = 0L;
        TestUtils.setEntityId(forbiddenLinkId, forbiddenLink);

        given(linkRepository.findById(forbiddenLink.getId())).willReturn(Optional.of(forbiddenLink));

        // when
        ResultActions actions = mockMvc.perform(
                get("/links/{linkId}", forbiddenLinkId)
                        .with(csrf())
                        .header("Authorization", "Bearer " + accessToken)
        );

        // then
        actions.andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value(LINK_FORBIDDEN.getMessage()));
    }

    @Test
    public void getReminder_Success() throws Exception {
        // given
        LinkListResult linkListResult = testDataRepository.createReminderResult();

        given(linkService.getReminder(principalDetails.getUserId())).willReturn(linkListResult);

        // when
        ResultActions actions = mockMvc.perform(
                get("/links/reminder")
                        .with(csrf())
                        .header("Authorization", "Bearer " + accessToken)
        );

        // then
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.linkList[0].title").value(linkListResult.getLinkList().get(0).getTitle()));
    }

    @Test
    public void updateLink_Success() throws Exception {
        // given
        Category category = testDataRepository.createCategory();

        Link link = testDataRepository.createLink(category, user);
        Long linkId = 0L;
        TestUtils.setEntityId(linkId, link);

        UpdateLinkRequest updateLinkRequest = testDataRepository.createUpdateLinkRequest();
        LinkResult linkResult = testDataRepository.createLinkResult();
        String successUpdateLinkRequestJson = objectMapper.writeValueAsString(updateLinkRequest);

        given(linkService.updateLink(linkId, updateLinkRequest, principalDetails.getUserId())).willReturn(linkResult);

        // when
        ResultActions actions = mockMvc.perform(
                patch("/links/{linkId}", linkId)
                        .with(csrf())
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(successUpdateLinkRequestJson)
        );

        // then
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.linkId").value(linkId));
    }

    @Test
    public void updateLink_Failed_Without_RequiredField() throws Exception {
        // given
        Category category = testDataRepository.createCategory();

        Link link = testDataRepository.createLink(category, user);
        Long linkId = 0L;
        TestUtils.setEntityId(linkId, link);

        UpdateLinkRequest updateLinkRequest = testDataRepository.createUpdateLinkRequest();
        LinkResult linkResult = testDataRepository.createLinkResult();

        updateLinkRequest.setUrl("");
        String failUpdateLinkRequestJson = objectMapper.writeValueAsString(updateLinkRequest);

        given(linkService.updateLink(linkId, updateLinkRequest, principalDetails.getUserId())).willReturn(linkResult);

        // when
        ResultActions actions = mockMvc.perform(
                patch("/links/{linkId}", linkId)
                        .with(csrf())
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(failUpdateLinkRequestJson)
        );

        // then
        MvcResult emptyUrlResult = actions.andExpect(status().isBadRequest()).andReturn();
        Assertions.assertThat(emptyUrlResult.getResolvedException()).isExactlyInstanceOf(MethodArgumentNotValidException.class);
    }

    @Test
    public void deleteLink_Success() throws Exception {
        // given
        Category category = testDataRepository.createCategory();

        Link link = testDataRepository.createLink(category, user);
        Long linkId = 0L;
        TestUtils.setEntityId(linkId, link);

        given(linkRepository.findById(linkId)).willReturn(Optional.of(link));
        willDoNothing().given(linkRepository).delete(link);

        // when
        ResultActions actions = mockMvc.perform(
                delete("/links/{linkId}", linkId)
                        .with(csrf())
                        .header("Authorization", "Bearer " + accessToken)
        );

        // then
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.linkId").value(linkId));
    }

    @Test
    public void deleteLink_Failed_With_NotExistingLink() throws Exception {
        // given
        Long notExistingLinkId = -1L;

        given(linkRepository.findById(notExistingLinkId)).willReturn(Optional.empty());

        // when
        ResultActions actions = mockMvc.perform(
                delete("/links/{linkId}", notExistingLinkId)
                        .with(csrf())
                        .header("Authorization", "Bearer " + accessToken)
        );

        // then
        MvcResult notExistingLinkResult = actions.andExpect(status().isNotFound()).andReturn();
        Assertions.assertThat(notExistingLinkResult.getResolvedException()).isExactlyInstanceOf(LinkException.class);
    }

    @Test
    public void deleteLink_Failed_With_ForbiddenLink() throws Exception {
        // given
        User anotherUser = testDataRepository.createUser();
        TestUtils.setEntityId(123L, anotherUser);
        Category pricateCategory = testDataRepository.createCategory();
        pricateCategory.setUser(anotherUser);

        Link forbiddenLink = testDataRepository.createLink(pricateCategory, anotherUser);
        Long forbiddenLinkId = 0L;
        TestUtils.setEntityId(forbiddenLinkId, forbiddenLink);

        given(linkRepository.findById(forbiddenLink.getId())).willReturn(Optional.of(forbiddenLink));

        // when
        ResultActions actions = mockMvc.perform(
                delete("/links/{linkId}", forbiddenLinkId)
                        .with(csrf())
                        .header("Authorization", "Bearer " + accessToken)
        );

        // then
        actions.andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value(LINK_FORBIDDEN.getMessage()));
    }
}