package com.example.board.toyboard.Service;

import com.example.board.toyboard.DTO.PageDTO;
import com.example.board.toyboard.DTO.PageListDTO;
import com.example.board.toyboard.DTO.PostListDTO;
import com.example.board.toyboard.DTO.SearchDTO;
import com.example.board.toyboard.Entity.Comment;
import com.example.board.toyboard.Entity.Post.Post;
import com.example.board.toyboard.Entity.Post.Recommendation;
import com.example.board.toyboard.Entity.User;
import com.example.board.toyboard.Entity.UserType;
import com.example.board.toyboard.Repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class PostServiceTest {

    @Autowired
    PostRepository postRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    ReportRepository reportRepository;

    @Autowired
    CommentService commentService;

    @Autowired
    RecommendationRepository recommendationRepository;

    @Autowired
    PostService postService;


    @Test
    void pageList() {

        User writer = User.builder()
                .loginId("writer")
                .nickname("writer")
                .password("tmpPassword")
                .email("writer@email.com")
                .userType(UserType.USER)
                .build();

        userRepository.save(writer);

        List<Post> posts = new ArrayList<>();

        IntStream.range(0, 30)
                .forEach(i -> posts.add(

                                Post.builder()
                                        .title("title" + i)
                                        .content("content" + i)
                                        .postType("free")
                                        .hits(0)
                                        .user(writer)
                                        .build()
                        )
                );

        postRepository.saveAll(posts);

        List<Comment> comments = new ArrayList<>();

        IntStream.range(0, 20)
                .forEach(
                        i -> IntStream.range(i,30)
                                .forEach(j -> comments.add(
                                                Comment.builder()
                                                        .post(posts.get(i))
                                                        .comment("comment" + i)
                                                        .user(writer)
                                                        .build()
                                        )

                                )
                );

        commentRepository.saveAll(comments);

        recommendationRepository.save(new Recommendation(writer, posts.get(29)));
        recommendationRepository.save(new Recommendation(writer, posts.get(28)));
        recommendationRepository.save(new Recommendation(writer, posts.get(27)));
        recommendationRepository.save(new Recommendation(writer, posts.get(26)));
        recommendationRepository.save(new Recommendation(writer, posts.get(25)));


        Pageable pageable = PageRequest.of(0, 10);
        SearchDTO searchDTO = new SearchDTO();

        searchDTO.setType("t");
        searchDTO.setContent("title");

        PageDTO<PostListDTO> dto = postService.makePageResult(pageable, searchDTO, "free");

        assertEquals(dto.getTotalPage(), 3);
        assertEquals(dto.getPageInfo().getNow(), 1);
        assertEquals(dto.getPageInfo().getStart(), 1);
        assertEquals(dto.getPageInfo().getEnd(), 3);
        assertFalse(dto.getPageInfo().isPrev());
        assertFalse(dto.getPageInfo().isNext());
        assertEquals(dto.getPageInfo().getPageList().size(), 3);
        assertIterableEquals(IntStream.rangeClosed(1, 3).boxed().toList(), dto.getPageInfo().getPageList());

        assertEquals(dto.getDtoList().size(), 10);

        System.out.println(dto);


    }

}