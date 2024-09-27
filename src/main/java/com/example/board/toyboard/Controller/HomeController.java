package com.example.board.toyboard.Controller;

import com.example.board.toyboard.DTO.LatestPosts;
import com.example.board.toyboard.DTO.PostTitle;
import com.example.board.toyboard.Service.PopularPostService;
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
    private final PopularPostService popularPostService;

    @GetMapping("/")
    public String home(Model model) {
        List<PostTitle> popularPost = popularPostService.getPopularPost();
        LatestPosts latestPosts = postService.getLatestPosts();

        model.addAttribute("popularPost", popularPost);
        model.addAttribute("latestPosts", latestPosts);

        return "post/home";
    }
}
