package com.example.board.toyboard.Controller;

import com.amazonaws.services.s3.AmazonS3;
import com.example.board.toyboard.DTO.*;
import com.example.board.toyboard.Entity.Post.Post;
import com.example.board.toyboard.Entity.Post.PostCode;
import com.example.board.toyboard.Entity.User;
import com.example.board.toyboard.Entity.UserType;
import com.example.board.toyboard.Service.CommentService;
import com.example.board.toyboard.Service.PostService;
import com.example.board.toyboard.Service.UserService;
import com.example.board.toyboard.file.FileStore;
import com.example.board.toyboard.file.UploadFile;
import com.example.board.toyboard.session.SessionConst;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
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
    private final AmazonS3 amazonS3;

    @Value("${aws.s3.bucket}")
    private String bucket;


    @GetMapping
    public String posts(PageListDTO pageListDTO, SearchDTO searchDTO
            , @RequestParam(defaultValue = "createdTime") String sort ,@RequestParam(defaultValue = "free") String postType, Model model) {

        Pageable pageable = pageListDTO.getPageable(Sort.by(Sort.Direction.DESC, sort));

        log.info("searchDTO = {}", searchDTO);
        log.info("sort={}", sort);


        model.addAttribute("posts", postService.makePageResult(pageable, searchDTO, postType.toUpperCase()));
        model.addAttribute("postType", postType);
        model.addAttribute("sort", sort);

        return "post/list";

    }



    @GetMapping("/write")
    public String writeStart(@ModelAttribute(name = "post") PostWriteDTO dto, Model model) {

        model.addAttribute("postCodes", makePostCodes());


        return "post/write";
    }


    @PostMapping("/write")
    public String writeEnd(@SessionAttribute(name = SessionConst.LOGIN_USER) String nickname,
                           @Valid @ModelAttribute(name = "post") PostWriteDTO dto, BindingResult bindingResult,
                           Model model) {


        if (bindingResult.hasErrors()) {

            model.addAttribute("postCodes", makePostCodes());
            return "post/write";
        }

        User loginUser = userService.findByNickname(nickname);

        Long postId = postService.write(dto, loginUser);


        return "redirect:/post/" + postId;


    }


    @GetMapping("/{postId}")
    public String readPost(
            @SessionAttribute(SessionConst.USER_TYPE) String userType,
            @PathVariable("postId") Long postId, Model model) {


        PostReadDTO readDTO = postService.read(postId);

        List<CommentReadDTO> commentDTOList = commentService.findComments(postId)
                                                .stream()
                                                .map(CommentReadDTO::new)
                                                .collect(Collectors.toList());




        model.addAttribute("post", readDTO);
        model.addAttribute("comments", commentDTOList);
        model.addAttribute("commentNums", commentDTOList.size());
        model.addAttribute("userType", userType);

        return "post/read";


    }


    @GetMapping("/update/{postId}")
    public String updateStart(@PathVariable("postId") Long postId,
                              @SessionAttribute(SessionConst.USER_TYPE) UserType userType,
                              @SessionAttribute(SessionConst.LOGIN_USER) String nickname,
                              Model model) {

        Post post = postService.findByIdWithUser(postId);

        postCheck(userType, nickname, post);


        model.addAttribute("id", postId);
        model.addAttribute("post", new PostUpdateDTO(post));
        model.addAttribute("postCodes", makePostCodes());


        return "post/update";
    }



    @PostMapping("/update/{postId}")
    public String updateEnd(@PathVariable("postId") Long postId,
                            @Valid @ModelAttribute(name = "post") PostUpdateDTO dto,
                            @SessionAttribute(SessionConst.USER_TYPE) UserType userType,
                            @SessionAttribute(SessionConst.LOGIN_USER) String nickname,
                            BindingResult bindingResult, Model model) {

        log.info("dto = {}", dto);

        Post post = postService.findByIdWithUser(postId);

        postCheck(userType, nickname,post);

        model.addAttribute("id", postId);

        if (bindingResult.hasErrors()) {
            return "post/update";
        }


        postService.update(post, dto);

        log.info("dto ={}", dto);

        return "redirect:/post/" + postId;

    }

    @GetMapping("/delete/{postId}")
    public String deletePost(@SessionAttribute(SessionConst.LOGIN_USER) String nickname,
                             @SessionAttribute(SessionConst.USER_TYPE) UserType userType,
                             @PathVariable Long postId) {
        Post post = postService.findByIdWithUser(postId);

        postCheck(userType, nickname, post);

        postService.delete(post);

        return "redirect:/post";

    }



    @GetMapping("/recommend/{postId}")
    @ResponseBody
    public int recommend(@SessionAttribute(SessionConst.LOGIN_USER) String nickname,  @PathVariable Long postId) {

        return postService.recommend(postId, nickname);

    }


    @PostMapping("/image")
    @ResponseBody
    public String uploadImage(@RequestParam MultipartFile file) throws IOException {

//        UploadFile uploadFile = fileStore.storeFile(file);
        UploadFile uploadFile = fileStore.saveFile(file);

        return "/post/image/" + uploadFile.getStoreFileName();

    }

    @ResponseBody
    @GetMapping("/image/{filename}")
    public UrlResource downloadImage(@PathVariable String filename) throws
            MalformedURLException {
//        return new UrlResource("file:" + fileStore.getFullPath(filename));
        return new UrlResource(amazonS3.getUrl(bucket, filename));
    }


    private List<PostCode> makePostCodes() {
        List<PostCode> postCodes = new ArrayList<>();

        postCodes.add(new PostCode("FREE", "자유"));
        postCodes.add(new PostCode("INFO", "정보"));
        postCodes.add(new PostCode("QNA", "Q&A"));

        return postCodes;
    }

    private void postCheck(UserType userType, String nickname, Post post) {
        if (!post.getUser().getNickname().equals(nickname) && !(userType == UserType.ADMIN)) {
            throw new RuntimeException();
        }
    }

}
