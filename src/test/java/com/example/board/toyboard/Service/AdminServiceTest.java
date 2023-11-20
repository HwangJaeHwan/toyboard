package com.example.board.toyboard.Service;

import com.example.board.toyboard.DTO.PageListDTO;
import com.example.board.toyboard.DTO.PageReportDTO;
import com.example.board.toyboard.DTO.PostReportDTO;
import com.example.board.toyboard.Entity.Post.Post;
import com.example.board.toyboard.Entity.Report.PostReport;
import com.example.board.toyboard.Entity.User;
import com.example.board.toyboard.Entity.UserType;
import com.example.board.toyboard.Repository.PostRepository;
import com.example.board.toyboard.Repository.ReportRepository;
import com.example.board.toyboard.Repository.UserRepository;
import org.junit.jupiter.api.Assertions;
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
    ReportRepository reportRepository;





    @Test
    void reportList() {

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
                                .recommendedNumber(0)
                                .postType("free")
                                .hits(0)
                                .commentNum(0)
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

        PageReportDTO<PostReportDTO> dto = adminService.makeReportedPostPage(new PageListDTO());

        assertEquals(dto.getStart(), 1);
        assertEquals(dto.getStart(), 1);
        assertEquals(dto.getEnd(), 2);
        assertFalse(dto.isPrev());
        assertFalse(dto.isNext());
        assertEquals(dto.getDtoList().size(), 10L);
        assertEquals(dto.getPageList().size(), 2L);

    }



}