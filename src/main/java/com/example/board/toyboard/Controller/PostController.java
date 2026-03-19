package com.example.board.toyboard.Controller;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.example.board.toyboard.DTO.*;
import com.example.board.toyboard.Entity.Post.Post;
import com.example.board.toyboard.Entity.Post.PostCode;
import com.example.board.toyboard.Entity.Post.PostType;
import com.example.board.toyboard.Entity.User;
import com.example.board.toyboard.Entity.UserType;
import com.example.board.toyboard.Service.*;
import com.example.board.toyboard.file.FileStore;
import com.example.board.toyboard.file.UploadFile;
import com.example.board.toyboard.response.ApiResponse;
import com.example.board.toyboard.session.SessionConst;
import com.example.board.toyboard.session.UserSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;
    private final UserService userService;
    private final CommentService commentService;
    private final ReportService reportService;
    private final FileStore fileStore;
    private final AmazonS3 amazonS3;
    private final PopularPostService popularPostService;
    private final PostRedisService postRedisService;

    @Value("${aws.s3.bucket}")
    private String bucket;

    @GetMapping("/")
    public String home(Model model) {

        log.info("헤헤");

//        List<PostTitle> popularPost = popularPostService.getPopularPost();
        List<HomePost> popularPost = postRedisService.getPopularPost();

        LatestPosts latestPosts = postService.getLatestPosts();

        model.addAttribute("popularPost", popularPost);
        model.addAttribute("latestPosts", latestPosts);

        return "post/home";
    }


    @GetMapping

    public String posts(PageListDTO pageListDTO
            , @RequestParam(defaultValue = "createdTime") String sort
            , @RequestParam(defaultValue = "FREE") PostType postType, Model model) {


        Pageable pageable = pageListDTO.getPageable(Sort.by(Sort.Direction.DESC, sort));


        model.addAttribute("searchDTO", new SearchDTO());
        model.addAttribute("posts", postService.makePageResult(pageable, postType));
        model.addAttribute("postType", postType);
        model.addAttribute("sort", sort);

        return "post/list";

    }

    @GetMapping("/search")
    String search(PageListDTO pageListDTO, SearchDTO searchDTO
            , @RequestParam(defaultValue = "createdTime") String sort
            , @RequestParam(defaultValue = "FREE") PostType postType
            , Model model) {

        Pageable pageable = pageListDTO.getPageable(Sort.by(Sort.Direction.DESC, sort));

        model.addAttribute("posts", new PageDTO<>(postService.search(pageable, searchDTO, postType)));
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
    public String writeEnd(UserSession userSession,
                           @Valid @ModelAttribute(name = "post") PostWriteDTO dto, BindingResult bindingResult,
                           Model model) {

        if (bindingResult.hasErrors()) {

            model.addAttribute("postCodes", makePostCodes());
            return "post/write";
        }


        Long postId = postService.write(dto, userSession.getId());


        return "redirect:+/posts/" + postId;


    }


    @GetMapping("/{postId}")
    public String readPost(
            UserSession userSession,
            @PathVariable("postId") Long postId, Model model) {


        PostReadDTO postResponse = postService.read(postId, userSession.getId());
        List<CommentReadDTO> commentsResponse = commentService.findComments(postId);


        model.addAttribute("post", postResponse);
        model.addAttribute("comments", commentsResponse);
        model.addAttribute("commentNums", commentsResponse.size());
        model.addAttribute("userType", userSession.getUserType());

        popularPostService.incrementPostView(postId);

        return "post/read";


    }


    @GetMapping("/{postId}/update")
    public String updateStart(@PathVariable("postId") Long postId,
                              UserSession userSession,
                              Model model) {



        model.addAttribute("post",
                postService.getUpdateForm(postId, userSession.getId(),userSession.getUserType()));
        model.addAttribute("postCodes", makePostCodes());


        return "post/update";
    }



    @PostMapping("/{postId}/update")
    public String updateEnd(@PathVariable("postId") Long postId,
                            UserSession userSession,
                            @Valid @ModelAttribute(name = "post") PostUpdateDTO dto,
                            BindingResult bindingResult, Model model) {

        log.info("dto = {}", dto);


        if (bindingResult.hasErrors()) {
            return "post/update";
        }


        postService.update(postId, userSession.getId(), userSession.getUserType(), dto);

        log.info("dto ={}", dto);

        return "redirect:/posts/" + postId;

    }

    @GetMapping("/{postId}/delete")
    public String deletePost(@PathVariable Long postId,
                             UserSession userSession) {


        postService.delete(userSession.getId(),userSession.getUserType(),postId);

        return "redirect:/posts";

    }

    @PatchMapping("/{postId}/reports")
    @ResponseBody
    public ApiResponse<Long> report(@PathVariable Long postId, UserSession userSession) {

        return reportService.postReport(userSession.getId(), postId);

    }




    @GetMapping("/{postId}/recommends")
    @ResponseBody
    public int recommend(@PathVariable Long postId, UserSession userSession) {

        return postService.recommend(postId, userSession.getId());

    }


    @PostMapping("/image")
    @ResponseBody
    public String uploadImage(@RequestParam MultipartFile file) throws IOException {

        UploadFile uploadFile = fileStore.storeFile(file);
//        UploadFile uploadFile = fileStore.saveFile(file);

        return "/posts/image/" + uploadFile.getStoreFileName();

    }


//    @ResponseBody
//    @GetMapping("/image/{filename}")
//    public ResponseEntity<Resource> downloadImage(@PathVariable String filename) throws IOException {
//
//        S3Object s3Object = amazonS3.getObject(bucket, filename);
//        S3ObjectInputStream inputStream = s3Object.getObjectContent();
//
//        String contentType = determineContentType(filename);
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.parseMediaType(contentType));
//
//        return new ResponseEntity<>(new InputStreamResource(inputStream), headers, HttpStatus.OK);
//    }

    @ResponseBody
    @GetMapping("/image/{filename}")
    public Resource downloadImage(@PathVariable String filename) throws IOException {

        return new UrlResource("file:" + fileStore.getFullPath(filename));
    }


    private String determineContentType(String filename) {
        if (filename.toLowerCase().endsWith(".png")) {
            return "image/png";
        } else if (filename.toLowerCase().endsWith(".jpg") || filename.toLowerCase().endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (filename.toLowerCase().endsWith(".gif")) {
            return "image/gif";
        } else {
            return "application/octet-stream";
        }
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
