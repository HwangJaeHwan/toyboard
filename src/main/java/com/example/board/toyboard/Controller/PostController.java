package com.example.board.toyboard.Controller;

import com.example.board.toyboard.DTO.*;
import com.example.board.toyboard.Entity.Post;
import com.example.board.toyboard.Entity.User;
import com.example.board.toyboard.Service.CommentService;
import com.example.board.toyboard.Service.PostService;
import com.example.board.toyboard.Service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    private final CommentService commentService;


    @GetMapping
    public String postList(PageListDTO pageListDTO, Model model) {

        Pageable pageable = pageListDTO.getPageable(Sort.by("createTime").descending());


//        List<PostListDTO> dtoList = postService.findAll().stream().map(post -> new PostListDTO(post, post.getUser().getNickname())).collect(Collectors.toList());

        model.addAttribute("posts", postService.makePageResult(pageable));

        return "/post/list";

    }


    @GetMapping("/write")
    public String writeStart(@ModelAttribute(name = "post") PostWriteDTO dto) {


        return "/post/write";
    }


    @PostMapping("/write")
    public String writeEnd(@SessionAttribute("loginUser") String nickname, @Valid @ModelAttribute(name = "post") PostWriteDTO dto, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "/post/write";
        }

        User loginUser = userService.findByNickname(nickname);

        Long postId = postService.write(dto, loginUser);


        return "redirect:/post/" + postId;


    }


    @GetMapping("/{postId}")
    public String readPost(@SessionAttribute("loginUser") String loginUser, @PathVariable("postId") Long postId, Model model) {


        Post post = postService.findById(postId);


        PostReadDTO readDTO = new PostReadDTO(post);

        List<CommentReadDTO> commentDTOList = commentService.findComments(post).stream().map(comment -> comment.makeReadDTO(comment.getUser().getNickname())).collect(Collectors.toList());
        model.addAttribute("commentWriteDTO", new CommentWriteDTO());
        model.addAttribute("post", readDTO);
        model.addAttribute("comments", commentDTOList);
        model.addAttribute("commentNums", commentDTOList.size());
        model.addAttribute("nickname", loginUser);

        return "/post/read";


    }

    @GetMapping("/update/{postId}")
    public String updateStart(@PathVariable("postId") Long postId, Model model) {

        Post post = postService.findById(postId);

        PostUpdateDTO dto = PostUpdateDTO.builder()
                .title(post.getTitle())
                .content(post.getContent())
                .build();

        model.addAttribute("id", postId);
        model.addAttribute("post", dto);

        return "/post/update";
    }


    @PostMapping("update/{postId}")
    public String updateEnd(@PathVariable("postId") Long postId, @Valid @ModelAttribute(name = "post") PostUpdateDTO dto, BindingResult bindingResult, Model model) {

        model.addAttribute("id", postId);

        if (bindingResult.hasErrors()) {
            return "/post/update";
        }


        postService.update(postId, dto);

        log.info("dto ={}", dto);

        return "redirect:/post/" + postId;

    }
}
