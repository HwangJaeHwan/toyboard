package com.example.board.toyboard.controller;
import com.example.board.toyboard.DTO.*;
import com.example.board.toyboard.entity.User;
import com.example.board.toyboard.exception.*;
import com.example.board.toyboard.service.UserService;
import com.example.board.toyboard.session.SessionConst;
import com.example.board.toyboard.session.UserSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;


@Controller
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;



    @GetMapping("/register")
    public String registerStart(@ModelAttribute(name = "user") RegisterDTO registerDTO) {

        return "user/register";
    }

    @PostMapping("/register")
    public String registerEnd(@Valid @ModelAttribute(name = "user") RegisterDTO registerDTO, BindingResult bindingResult) {

        log.info("loginId={}", registerDTO.getLoginId());

        if (bindingResult.hasErrors()) {
            return "user/register";
        }

        try {
            userService.save(registerDTO);
            return "redirect:/login";
        } catch (PasswordMismatchException e) {
            bindingResult.rejectValue("passwordCheck", "passwordMismatch", "비밀번호가 다릅니다.");
        } catch (DuplicateLoginIdException e) {
            bindingResult.rejectValue("loginId", "duplicateLoginId", "아이디가 중복됩니다.");
        } catch (DuplicateNicknameException e) {
            bindingResult.rejectValue("nickname", "duplicateNickname", "닉네임이 중복됩니다.");
        } catch (DuplicateEmailException e) {
            bindingResult.rejectValue("email", "duplicateEmail", "이메일이 중복됩니다.");
        }


        return "user/register";
    }

    @GetMapping("/login")
    public String loginStart(User user) {


        return "user/login";
    }

    @PostMapping("/login")
    public String loginEnd(@Valid @ModelAttribute("user") LoginDTO loginDTO,
                           BindingResult bindingResult,
                           HttpServletRequest request,
                           @RequestParam(defaultValue = "/") String redirectURI) {

        if (bindingResult.hasErrors()) {
            return "user/login";
        }

        try {
            UserSession userSession = userService.login(loginDTO);

            HttpSession session = request.getSession();
            session.setAttribute(SessionConst.LOGIN_USER, userSession);

            return "redirect:" + redirectURI;

        } catch (LoginFailException e) {
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 일치하지 않습니다.");
            return "user/login";
        }
    }


    @GetMapping("/logout")
    public String logout(HttpServletRequest request){

        HttpSession session = request.getSession(false);


        if (session != null) {
            session.invalidate();
        }


        return "redirect:/login";
    }


    @GetMapping("/mypage")
    public String myPage(UserSession userSession) {

        return "redirect:/mypage/" + userSession.getId();
    }


    @GetMapping("/mypage/{id}")
    public String myPageWithId(@PathVariable Long id, PageListDTO pageListDTO, Model model) {
        model.addAttribute("myPage", userService.findLogsByUser(id, pageListDTO.getPageable()));
        return "user/mypage";
    }

    @GetMapping("/mypage/{id}/posts")
    public String myPagePosts(@PathVariable Long id, PageListDTO pageListDTO, Model model) {
        model.addAttribute("myPage", userService.findPostLogsByUser(id, pageListDTO.getPageable()));
        return "user/mypagePost";
    }



    @GetMapping("/mypage/edit")
    public String editStart(UserSession userSession, Model model) {


        model.addAttribute("user", new UserEditDTO(userSession.getNickname()));


        return "user/edit";

    }

    @PostMapping("mypage/edit")
    public String editEnd(UserSession userSession, UserEditDTO userEditDTO,
                          BindingResult bindingResult,HttpServletRequest request) {

        if (bindingResult.hasErrors()) {
            return "user/edit";
        }

        try {
            UserSession newSession = userService.nicknameChange(userSession, userEditDTO);

            HttpSession session = request.getSession(false);
            session.setAttribute(SessionConst.LOGIN_USER, newSession);

            return "redirect:/mypage/" + newSession.getId();

        } catch (DuplicateNicknameException e) {
            bindingResult.rejectValue("nickname", "duplicateNickname", "닉네임이 중복됩니다.");
            return "user/edit";
        }


    }


    @GetMapping("/mypage/passwordChange")
    public String passwordChangeStart(@ModelAttribute("password") PasswordChangeDTO dto) {


        return "user/passwordChange";
    }

    @PostMapping("/mypage/passwordChange")
    public String passwordChangeEnd(UserSession userSession,
                                    @ModelAttribute("password") PasswordChangeDTO dto,
                                    BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "user/passwordChange";
        }

        try {
            userService.passwordChange(userSession.getId(), dto);
            return "redirect:/mypage/" + userSession.getId();

        } catch (InvalidPasswordException e) {
            bindingResult.rejectValue("nowPassword", "difPassword", e.getMessage());
        } catch (SamePasswordException e) {
            bindingResult.rejectValue("newPassword", "samePassword", e.getMessage());
        } catch (PasswordMismatchException e) {
            bindingResult.rejectValue("newPasswordCheck", "mismatchPassword", e.getMessage());
        }

        return "user/passwordChange";
    }




}
