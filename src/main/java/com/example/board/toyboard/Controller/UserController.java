package com.example.board.toyboard.Controller;
import com.example.board.toyboard.DTO.LoginDTO;
import com.example.board.toyboard.Entity.User;
import com.example.board.toyboard.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;


@Controller
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;


    @GetMapping("/login")
    public String loginStart(User user) {


        return "user/login";
    }

    @PostMapping("/login")
    public String loginEnd(@Valid @ModelAttribute LoginDTO loginDTO, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "user/login";
        }

        

        return "/";
    }
}
