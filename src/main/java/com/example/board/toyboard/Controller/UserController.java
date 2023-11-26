package com.example.board.toyboard.Controller;
import com.example.board.toyboard.DTO.*;
import com.example.board.toyboard.Entity.User;
import com.example.board.toyboard.Entity.log.Log;
import com.example.board.toyboard.Service.UserService;
import com.example.board.toyboard.session.SessionConst;
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


@Controller
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    @GetMapping("/")
    public String toPosts(){

        return "redirect:/post";
    }


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

        Long registerId = userService.save(registerDTO);


        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginStart(User loginUser) {


        return "user/login";
    }

    @PostMapping("/login")
    public String loginEnd(@Valid @ModelAttribute(name = "user") LoginDTO loginDTO, BindingResult bindingResult,
                           HttpServletRequest request,@RequestParam(defaultValue = "/post") String redirectURI) {

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

        session.setAttribute(SessionConst.LOGIN_USER, loginUser.getNickname());
        session.setAttribute(SessionConst.USER_TYPE, loginUser.getUserType());


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
    public String myPage(@SessionAttribute(name = SessionConst.LOGIN_USER) String nickname) {

        User user = userService.findByNickname(nickname);

        return "redirect:/mypage/" + user.getId();
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
    public String editStart(@SessionAttribute(name = SessionConst.LOGIN_USER) String nickname, Model model) {


        model.addAttribute("user", new UserEditDTO(nickname));


        return "user/edit";

    }

    @PostMapping("mypage/edit")
    public String editEnd(@SessionAttribute(name = SessionConst.LOGIN_USER) String nickname, UserEditDTO userEditDTO,
                          BindingResult bindingResult,HttpServletRequest request) {

        if (bindingResult.hasErrors()) {
            return "user/edit";
        }

        if (userService.nicknameDuplicationCheck(userEditDTO.getNickname())) {
            bindingResult.rejectValue("nickname", "duplicateNickname", "닉네임이 중복됩니다.");
            return "user/edit";
        }


        Long userId = userService.nicknameChange(nickname, userEditDTO);

        HttpSession session = request.getSession(false);
        session.setAttribute(SessionConst.LOGIN_USER, userEditDTO.getNickname());
        return "redirect:/mypage/" + userId;


    }


    @GetMapping("/mypage/passwordChange")
    public String passwordChangeStart(@ModelAttribute("password") PasswordChangeDTO dto) {


        return "user/passwordChange";
    }

    @PostMapping("/mypage/passwordChange")
    public String passwordChangeEnd(@SessionAttribute(name = SessionConst.LOGIN_USER) String nickname,@ModelAttribute("password") PasswordChangeDTO dto,
                                    BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "user/passwordChange";
        }

        User loginUser = userService.findByNickname(nickname);

        if (!userService.isPassword(dto.getNowPassword(), loginUser.getPassword())) {
            bindingResult.rejectValue("nowPassword", "difPassword", "현재 비밀번호가 틀립니다.");
            return "user/passwordChange";
        }

        if (dto.getNowPassword().equals(dto.getNewPassword())) {
            bindingResult.rejectValue("newPassword", "samePassword", "현재 비밀번호와 새 비밀번호가 동일합니다.");
            return "user/passwordChange";
        }

        if (!dto.getNewPassword().equals(dto.getNewPasswordCheck())) {
            bindingResult.rejectValue("newPassword", "samePassword", "비밀번호가 일치하지 않습니다.");
            return "user/passwordChange";
        }

        userService.passwordChange(nickname, dto);

        return "redirect:/mypage/"+loginUser.getId();


    }




}
