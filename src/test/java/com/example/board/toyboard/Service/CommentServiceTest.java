package com.example.board.toyboard.Service;

import com.example.board.toyboard.DTO.CommentReadDTO;
import com.example.board.toyboard.Entity.*;
import com.example.board.toyboard.Entity.Post.Post;
import com.example.board.toyboard.Entity.Report.CommentReport;
import com.example.board.toyboard.Entity.Report.PostReport;
import com.example.board.toyboard.Repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CommentServiceTest {


    @Autowired
    PostRepository postRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    ReportRepository reportRepository;

    @Autowired
    UpRepository upRepository;

    @Autowired
    DownRepository downRepository;

    @Autowired
    CommentService commentService;


    @Test
    void commentReadList() {

        User writer = User.builder()
                .loginId("writer")
                .nickname("writer")
                .password("tmpPassword")
                .email("writer@email.com")
                .userType(UserType.USER)
                .build();

        User up1 = User.builder()
                .loginId("up1")
                .nickname("up1")
                .password("tmpPassword")
                .email("up1@email.com")
                .userType(UserType.USER)
                .build();

        User up2 = User.builder()
                .loginId("up2")
                .nickname("up2")
                .password("tmpPassword")
                .email("up2@email.com")
                .userType(UserType.USER)
                .build();

        User up3 = User.builder()
                .loginId("up3")
                .nickname("up3")
                .password("tmpPassword")
                .email("up3@email.com")
                .userType(UserType.USER)
                .build();

        User down1 = User.builder()
                .loginId("down1")
                .nickname("down1")
                .password("tmpPassword")
                .email("down1@email.com")
                .userType(UserType.USER)
                .build();

        User down2 = User.builder()
                .loginId("down2")
                .nickname("down2")
                .password("tmpPassword")
                .email("down2@email.com")
                .userType(UserType.USER)
                .build();

        userRepository.save(writer);
        userRepository.save(up1);
        userRepository.save(up2);
        userRepository.save(up3);
        userRepository.save(down1);
        userRepository.save(down2);

        Post post = Post.builder()
                .title("title")
                .content("content")
                .recommendedNumber(0)
                .postType("free")
                .hits(0)
                .commentNum(0)
                .user(writer)
                .build();

        postRepository.save(post);

        Comment comment = Comment.builder()
                .comment("content")
                .user(writer)
                .post(post)
                .build();

        commentRepository.save(comment);

        upRepository.save(new Up(up1, comment));
        upRepository.save(new Up(up2, comment));
        upRepository.save(new Up(up3, comment));

        downRepository.save(new Down(down1, comment));
        downRepository.save(new Down(down2, comment));

        reportRepository.save(new CommentReport(down1, comment));
        reportRepository.save(new CommentReport(down2, comment));

        List<CommentReadDTO> dto = commentService.test(post);

        System.out.println(dto);


    }

}