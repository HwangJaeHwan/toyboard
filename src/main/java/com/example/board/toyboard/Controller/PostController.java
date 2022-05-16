package com.example.board.toyboard.Controller;

import com.example.board.toyboard.DTO.*;
import com.example.board.toyboard.Entity.Post.Post;
import com.example.board.toyboard.Entity.Post.PostCode;
import com.example.board.toyboard.Entity.User;
import com.example.board.toyboard.Service.CommentService;
import com.example.board.toyboard.Service.PostService;
import com.example.board.toyboard.Service.UserService;
import com.example.board.toyboard.file.FileStore;
import com.example.board.toyboard.file.UploadFile;
import com.example.board.toyboard.session.SessionConst;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
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
    private final FileStore fileStore;


    @GetMapping
    public String postList(PageListDTO pageListDTO, SearchDTO searchDTO, Model model) {

        Pageable pageable = pageListDTO.getPageable(Sort.by("createTime").descending());

        log.info("searchDTO = {}", searchDTO);

        model.addAttribute("posts", postService.makePageResult(pageable, searchDTO));


        return "/post/list";

    }


    @GetMapping("/write")
    public String writeStart(@ModelAttribute(name = "post") PostWriteDTO dto, Model model) {

        model.addAttribute("postCodes", makePostCodes());


        return "/post/write";
    }



    @PostMapping("/write")
    public String writeEnd(@SessionAttribute(name = SessionConst.LOGIN_USER) String nickname, @Valid @ModelAttribute(name = "post") PostWriteDTO dto, BindingResult bindingResult) {


        if (bindingResult.hasErrors()) {
            return "/post/write";
        }

        User loginUser = userService.findByNickname(nickname);

        Long postId = postService.write(dto, loginUser);


        return "redirect:/post/" + postId;


    }


    @GetMapping("/{postId}")
    public String readPost(@SessionAttribute(name = SessionConst.LOGIN_USER) String loginUser, @PathVariable("postId") Long postId, Model model, @RequestParam(required = false) boolean check) {


        Post post = postService.findById(postId);

        if (!check) {
            post.addHits();
        }


        PostReadDTO readDTO = new PostReadDTO(post);

        List<CommentReadDTO> commentDTOList = commentService.findComments(post).stream().map(comment -> new CommentReadDTO(comment)).collect(Collectors.toList());
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
                .postType(post.getPostType())
                .build();

        model.addAttribute("id", postId);
        model.addAttribute("post", dto);
        model.addAttribute("postCodes", makePostCodes());


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

    @PostMapping("/image")
    @ResponseBody
    public String uploadImage(@RequestParam MultipartFile file) throws IOException {

        UploadFile uploadFile = fileStore.storeFile(file);

        String imagePath = "/post/image/" + uploadFile.getStoreFileName();

        return imagePath;

    }

    @ResponseBody
    @GetMapping("/image/{filename}")
    public UrlResource downloadImage(@PathVariable String filename) throws
            MalformedURLException {
        return new UrlResource("file:" + fileStore.getFullPath(filename));
    }


    private List<PostCode> makePostCodes() {
        List<PostCode> postCodes = new ArrayList<>();

        postCodes.add(new PostCode("FREE", "자유"));
        postCodes.add(new PostCode("INFO", "정보"));
        postCodes.add(new PostCode("QNA", "Q&A"));

        return postCodes;
    }

}
