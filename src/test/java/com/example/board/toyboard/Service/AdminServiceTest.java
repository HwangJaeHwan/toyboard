package com.example.board.toyboard.Service;

import com.example.board.toyboard.DTO.CommentReportDTO;
import com.example.board.toyboard.DTO.PageListDTO;
import com.example.board.toyboard.DTO.PageDTO;
import com.example.board.toyboard.DTO.PostReportDTO;
import com.example.board.toyboard.Entity.Comment;
import com.example.board.toyboard.Entity.Post.Post;
import com.example.board.toyboard.Entity.Report.CommentReport;
import com.example.board.toyboard.Entity.Report.PostReport;
import com.example.board.toyboard.Entity.User;
import com.example.board.toyboard.Entity.UserType;
import com.example.board.toyboard.Repository.CommentRepository;
import com.example.board.toyboard.Repository.PostRepository;
import com.example.board.toyboard.Repository.ReportRepository;
import com.example.board.toyboard.Repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class AdminServiceTest {


    @Autowired
    AdminService adminService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    ReportRepository reportRepository;





    @Test
    void reportPostList() {

        User admin = User.builder()
                .loginId("adminId")
                .nickname("admin")
                .password("tmpPassword")
                .email("admin@email.com")
                .userType(UserType.ADMIN)
                .build();

        User writer = User.builder()
                .loginId("writer")
                .nickname("writer")
                .password("tmpPassword")
                .email("writer@email.com")
                .userType(UserType.USER)
                .build();


        List<User> users = new ArrayList<>();


        IntStream.range(0, 20)
                .forEach(i -> users.add(User.builder()
                        .loginId("user" + i)
                        .nickname("user" + i)
                        .password("tmpPassword")
                        .email("user@email.com" + i)
                        .userType(UserType.USER)
                        .build()));

        

        userRepository.save(admin);
        userRepository.save(writer);
        userRepository.saveAll(users);


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

        List<PostReport> reports = new ArrayList<>();

        IntStream.range(0,20)
                .forEach(i ->
                        IntStream.range(0,20)
                                .forEach(j -> reports.add(new PostReport(users.get(i), posts.get(j))))
                );

        reportRepository.saveAll(reports);

        PageDTO<PostReportDTO> dto = adminService.makeReportedPostPage(new PageListDTO());

        assertEquals(dto.getPageInfo().getStart(), 1);
        assertEquals(dto.getPageInfo().getStart(), 1);
        assertEquals(dto.getPageInfo().getEnd(), 2);
        assertFalse(dto.getPageInfo().isPrev());
        assertFalse(dto.getPageInfo().isNext());
        assertEquals(dto.getDtoList().size(), 10L);
        assertEquals(dto.getPageInfo().getPageList().size(), 2L);

    }

    @Test
    void reportCommentList() {

        User admin = User.builder()
                .loginId("adminId")
                .nickname("admin")
                .password("tmpPassword")
                .email("admin@email.com")
                .userType(UserType.ADMIN)
                .build();

        User writer = User.builder()
                .loginId("writer")
                .nickname("writer")
                .password("tmpPassword")
                .email("writer@email.com")
                .userType(UserType.USER)
                .build();


        List<User> users = new ArrayList<>();


        IntStream.range(0, 20)
                .forEach(i -> users.add(User.builder()
                        .loginId("user" + i)
                        .nickname("user" + i)
                        .password("tmpPassword")
                        .email("user@email.com" + i)
                        .userType(UserType.USER)
                        .build()));



        userRepository.save(admin);
        userRepository.save(writer);
        userRepository.saveAll(users);


        Post post = Post.builder()
                .title("title")
                .content("content")
                .postType("free")
                .hits(0)
                .user(writer)
                .build();

        postRepository.save(post);

        Comment comment = Comment.builder()
                .comment("content")
                .user(writer)
                .post(post)
                .build();

        commentRepository.save(comment);




        List<CommentReport> reports = new ArrayList<>();


        IntStream.range(0, 20)
                .forEach(i -> IntStream.range(i, 30)
                        .forEach(
                                j -> reports.add(new CommentReport(users.get(i), comment))
                        ));

        reportRepository.saveAll(reports);

        PageDTO<CommentReportDTO> dto = adminService.makeReportCommentPage(new PageListDTO());

        System.out.println(dto);


    }



}