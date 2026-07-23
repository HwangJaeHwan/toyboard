package com.example.board.toyboard.controller;

import com.example.board.toyboard.DTO.LoginDTO;
import com.example.board.toyboard.DTO.RegisterDTO;
import com.example.board.toyboard.entity.UserType;
import com.example.board.toyboard.exception.*;
import com.example.board.toyboard.service.UserService;
import com.example.board.toyboard.session.SessionConst;
import com.example.board.toyboard.session.UserSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserService userService;

    @MockBean
    JpaMetamodelMappingContext jpaMetamodelMappingContext;




    @DisplayName("회원가입 페이지를 조회한다")
    @Test
    void registerStart() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/register"))
                .andExpect(model().attributeExists("user"));
    }

    @DisplayName("회원가입 성공 시 로그인 페이지로 이동한다")
    @Test
    void registerEnd_success() throws Exception {
        mockMvc.perform(post("/register")
                        .param("loginId", "user1")
                        .param("password", "Password1234!")
                        .param("passwordCheck", "Password1234!")
                        .param("nickname", "tester")
                        .param("email", "test@test.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        then(userService).should().save(any(RegisterDTO.class));
    }

    @DisplayName("회원가입 실패 - 비밀번호 확인 불일치")
    @Test
    void registerEnd_passwordMismatch() throws Exception {
        willThrow(new PasswordMismatchException())
                .given(userService)
                .save(any(RegisterDTO.class));

        performValidRegister()
                .andExpect(status().isOk())
                .andExpect(view().name("user/register"))
                .andExpect(model().attributeHasFieldErrors("user", "passwordCheck"));
    }

    @DisplayName("회원가입 실패 - 로그인 아이디 중복")
    @Test
    void registerEnd_duplicateLoginId() throws Exception {
        willThrow(new DuplicateLoginIdException())
                .given(userService)
                .save(any(RegisterDTO.class));

        performValidRegister()
                .andExpect(status().isOk())
                .andExpect(view().name("user/register"))
                .andExpect(model().attributeHasFieldErrors("user", "loginId"));
    }

    @DisplayName("회원가입 실패 - 닉네임 중복")
    @Test
    void registerEnd_duplicateNickname() throws Exception {
        willThrow(new DuplicateNicknameException())
                .given(userService)
                .save(any(RegisterDTO.class));

        performValidRegister()
                .andExpect(status().isOk())
                .andExpect(view().name("user/register"))
                .andExpect(model().attributeHasFieldErrors("user", "nickname"));
    }

    @DisplayName("회원가입 실패 - 이메일 중복")
    @Test
    void registerEnd_duplicateEmail() throws Exception {
        willThrow(new DuplicateEmailException())
                .given(userService)
                .save(any(RegisterDTO.class));

        performValidRegister()
                .andExpect(status().isOk())
                .andExpect(view().name("user/register"))
                .andExpect(model().attributeHasFieldErrors("user", "email"));
    }

    private ResultActions performValidRegister() throws Exception {
        return mockMvc.perform(post("/register")
                .param("loginId", "user1234")
                .param("password", "Password1234!")
                .param("passwordCheck", "Password1234!")
                .param("nickname", "tester")
                .param("email", "test@test.com"));
    }


    @DisplayName("로그인 페이지를 조회한다")
    @Test
    void loginStart() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/login"));
    }

    @DisplayName("로그인 성공 시 세션에 로그인 유저를 저장하고 redirect 한다")
    @Test
    void loginEnd_success() throws Exception {
        UserSession session = UserSession.builder()
                .id(1L)
                .nickname("tester")
                .userType(UserType.USER)
                .build();

        given(userService.login(any(LoginDTO.class)))
                .willReturn(session);

        mockMvc.perform(post("/login")
                        .param("loginId", "user1")
                        .param("password", "1234")
                        .param("redirectURI", "/posts"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts"))
                .andExpect(request().sessionAttribute(SessionConst.LOGIN_USER, session));
    }

    @DisplayName("로그인 실패 시 로그인 페이지로 돌아간다")
    @Test
    void loginEnd_fail() throws Exception {
        given(userService.login(any(LoginDTO.class)))
                .willThrow(new LoginFailException());

        mockMvc.perform(post("/login")
                        .param("loginId", "wrong")
                        .param("password", "wrong"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/login"))
                .andExpect(model().attributeHasErrors("user"));
    }


    @DisplayName("로그아웃 시 세션을 만료하고 로그인 페이지로 redirect 한다")
    @Test
    void logout() throws Exception {
        MockHttpSession session = new MockHttpSession();

        mockMvc.perform(get("/logout").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"))
                .andExpect(request().sessionAttributeDoesNotExist(SessionConst.LOGIN_USER));

        assertThat(session.isInvalid()).isTrue();
    }
}
