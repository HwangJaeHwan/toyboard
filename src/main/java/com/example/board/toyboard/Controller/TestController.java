package com.example.board.toyboard.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class TestController {


    @GetMapping("/")
    public String test() {

        return "user/login";
    }
}
