package com.example.board.toyboard.Controller;

import com.example.board.toyboard.DTO.PostListDTO;
import com.example.board.toyboard.DTO.PostReadDTO;
import com.example.board.toyboard.DTO.PostWriteDTO;
import com.example.board.toyboard.Entity.Post;
import com.example.board.toyboard.Entity.User;
import com.example.board.toyboard.Repository.PostRepository;
import com.example.board.toyboard.Service.PostService;
import com.example.board.toyboard.Service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/post")
public class PostController {

    private final PostService postService;
    private final UserService userService;


    @GetMapping
    public String postList(Model model) {

        List<PostListDTO> dtoList = postService.findAll().stream().map(post -> post.makeListDTO(post.getUser().getNickname())).collect(Collectors.toList());

        model.addAttribute("posts",dtoList);

        return "/post/list";

    }


    @GetMapping("/write")
    public String writeStart(@ModelAttribute(name = "post") PostWriteDTO dto) {


        return "/post/write";
    }


    @PostMapping("/write")
    public String writeEnd(@SessionAttribute("loginUser") Long userId, @Valid @ModelAttribute(name = "post") PostWriteDTO dto, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "/post/write";
        }

        User loginUser = userService.findById(userId);

        Long postId = postService.write(dto, loginUser);


        return "redirect:/post/" + postId;


    }


    @GetMapping("/{postId}")
    public String readPost(@PathVariable("postId") Long postId, Model model) {


        Post post = postService.findById(postId);

        PostReadDTO readDTO = post.makeReadDTO(post.getUser().getNickname());

        model.addAttribute("post", readDTO);

        return "/post/read";


    }





}
