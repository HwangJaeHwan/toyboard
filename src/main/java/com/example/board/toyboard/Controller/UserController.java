package com.example.board.toyboard.Controller;
import com.example.board.toyboard.DTO.*;
import com.example.board.toyboard.Entity.User;
import com.example.board.toyboard.Entity.log.Log;
import com.example.board.toyboard.Exception.InvalidPasswordException;
import com.example.board.toyboard.Exception.PasswordMismatchException;
import com.example.board.toyboard.Exception.SamePasswordException;
import com.example.board.toyboard.Service.PopularPostService;
import com.example.board.toyboard.Service.UserService;
import com.example.board.toyboard.session.SessionConst;
import com.example.board.toyboard.session.UserSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import java.util.List;


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

        if (!userService.idPatternCheck(registerDTO.getLoginId())) {
            bindingResult.rejectValue("loginId", "idPatternCheck", "5~20자의 영문, 숫자만 사용 가능합니다.");
            return "user/register";
        }

        if (!userService.passwordPatternCheck(registerDTO.getPassword())) {
            bindingResult.rejectValue("loginId", "passwordPatternCheck", "숫자, 영어, 특수문자 포함 8~20자입니다.");
            return "user/register";
        }

        if (!registerDTO.getPassword().equals(registerDTO.getPasswordCheck())) {
            bindingResult.rejectValue("password", "passwordCheck", "비밀번호가 다릅니다.");
            return "user/register";
        }

        if (userService.loginIdDuplicationCheck(registerDTO.getLoginId())) {
            bindingResult.rejectValue("loginId", "duplicateLoginId", "아이디가 중복됩니다.");
            return "user/register";
        }
        if (userService.nicknameDuplicationCheck(registerDTO.getNickname())) {
            bindingResult.rejectValue("nickname", "duplicateNickname", "닉네임이 중복됩니다.");
            return "user/register";
        }

        if (userService.emailDuplicationCheck(registerDTO.getEmail())) {
            bindingResult.rejectValue("email", "duplicateEmail", "이메일이 중복됩니다.");
            return "user/register";
        }

//        bindingResult.hasErrors();

        Long registerId = userService.save(registerDTO);


        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginStart(User loginUser) {


        return "user/login";
    }

    @PostMapping("/login")
    public String loginEnd(@Valid @ModelAttribute(name = "user") LoginDTO loginDTO, BindingResult bindingResult,
                           HttpServletRequest request,@RequestParam(defaultValue = "/") String redirectURI) {

        if (bindingResult.hasErrors()) {
            return "user/login";
        }

        User loginUser = userService.login(loginDTO);

        if (loginUser == null) {
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 일치하지 않습니다.");
            return "user/login";
        }

        log.info("유저2={}", loginUser);


        HttpSession session = request.getSession();

        session.setAttribute(SessionConst.NICKNAME, loginUser.getNickname());
        session.setAttribute(SessionConst.USER_TYPE, loginUser.getUserType());
        session.setAttribute(SessionConst.USER_ID, loginUser.getId());

        log.info("닉네임={}",session.getAttribute(SessionConst.NICKNAME));
        log.info("타입={}",session.getAttribute(SessionConst.USER_TYPE));
        log.info("유저아이디={}",session.getAttribute(SessionConst.USER_ID));


        return "redirect:" + redirectURI;
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

        return "redirect:/mypage/" + userSession.getUserId();
    }


    @GetMapping("/mypage/{id}")
    public String myPageWithId(@PathVariable Long id, PageListDTO pageListDTO, Model model) {

        Pageable pageable = pageListDTO.getPageable();
        User user = userService.findById(id);

        PageConvertDTO<LogDTO, Log> result = userService.findLogsByUser(user, pageable);

        model.addAttribute("postNum", user.getPosts().size());
        model.addAttribute("nickname", user.getNickname());
        model.addAttribute("userId", user.getId());
        model.addAttribute("logs", result);

        return "user/mypage";
    }


    @GetMapping("/mypage/{id}/posts")
    public String myPagePosts(@PathVariable Long id, PageListDTO pageListDTO , Model model) {

        Pageable pageable = pageListDTO.getPageable();
        User user = userService.findById(id);

        PageConvertDTO<LogDTO, Log> result = userService.findPostLogsByUser(user, pageable);

        model.addAttribute("postNum", user.getPosts().size());
        model.addAttribute("nickname", user.getNickname());
        model.addAttribute("userId", user.getId());
        model.addAttribute("logs", result);

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

        if (userService.nicknameDuplicationCheck(userEditDTO.getNickname())) {
            bindingResult.rejectValue("nickname", "duplicateNickname", "닉네임이 중복됩니다.");
            return "user/edit";
        }


        Long userId = userService.nicknameChange(userSession.getNickname(), userEditDTO);

        HttpSession session = request.getSession(false);
        session.setAttribute(SessionConst.NICKNAME, userEditDTO.getNickname());
        return "redirect:/mypage/" + userId;


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
            userService.passwordChange(userSession.getUserId(), dto);
        } catch (InvalidPasswordException e) {
            bindingResult.rejectValue("nowPassword", "difPassword", e.getMessage());
            return "user/passwordChange";
        } catch (SamePasswordException e) {
            bindingResult.rejectValue("newPassword", "samePassword", e.getMessage());
            return "user/passwordChange";
        } catch (PasswordMismatchException e) {
            bindingResult.rejectValue("newPasswordCheck", "mismatchPassword", e.getMessage());
            return "user/passwordChange";
        }

        return "redirect:/mypage/"+userSession.getUserId();


    }




}
