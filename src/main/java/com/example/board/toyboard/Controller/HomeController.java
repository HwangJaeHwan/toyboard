package com.example.board.toyboard.Controller;

import com.example.board.toyboard.DTO.HomePost;
import com.example.board.toyboard.DTO.LatestPosts;
import com.example.board.toyboard.Service.PostRedisService;
import com.example.board.toyboard.Service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final PostService postService;
    private final PostRedisService postRedisService;

    @GetMapping("/")
    public String home(Model model) {
        List<HomePost> popularPost = postRedisService.getPopularPost();
        LatestPosts latestPosts = postService.getLatestPosts();

        model.addAttribute("popularPost", popularPost);
        model.addAttribute("latestPosts", latestPosts);

        return "post/home";
    }
}
