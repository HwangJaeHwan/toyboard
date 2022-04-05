package com.example.board.toyboard.Controller;
import com.example.board.toyboard.DTO.LoginDTO;
import com.example.board.toyboard.Entity.User;
import com.example.board.toyboard.Repository.UserRepository;
import com.example.board.toyboard.Service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;


@Controller
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    @GetMapping("/login")
    public String loginStart(User loginUser) {


        return "user/login";
    }

    @PostMapping("/login")
    public String loginEnd(@Valid @ModelAttribute(name = "user") LoginDTO loginDTO, BindingResult bindingResult, HttpServletRequest request) {

        if (bindingResult.hasErrors()) {
            return "user/login";
        }

        User loginUser = userService.login(loginDTO);

        if (loginUser == null) {
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 일치하지 않습니다.");
            return "user/login";
        }


        HttpSession session = request.getSession();

        session.setAttribute("loginUser", loginUser.getId());


        return "/";
    }
}
