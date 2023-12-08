package com.example.board.toyboard.Controller;

import com.example.board.toyboard.DTO.PageConvertDTO;
import com.example.board.toyboard.DTO.PageListDTO;
import com.example.board.toyboard.DTO.SearchDTO;
import com.example.board.toyboard.DTO.UserListDTO;
import com.example.board.toyboard.Entity.User;
import com.example.board.toyboard.Service.PostService;
import com.example.board.toyboard.Service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Controller
@Slf4j
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final PostService postService;


    @GetMapping("/users")
    public String users(PageListDTO pageListDTO, SearchDTO searchDTO, Model model) {
        Pageable pageable = pageListDTO.getPageable();
        PageConvertDTO<UserListDTO, User> pageDto = userService.makePageResult(pageable, searchDTO);


        log.info("dto = {}", pageDto);
        model.addAttribute("data", pageDto);

        return "auth/home";
    }

}
