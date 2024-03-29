package com.example.board.toyboard.Service;

import com.example.board.toyboard.DTO.*;
import com.example.board.toyboard.Entity.User;
import com.example.board.toyboard.Repository.CommentRepository;
import com.example.board.toyboard.Repository.PostRepository;
import com.example.board.toyboard.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Function;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AdminService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    public PageConvertDTO<UserListDTO, User> makeUserPage(Pageable pageable, SearchDTO searchDTO) {

        Page<User> search = userRepository.search(pageable, searchDTO);

        Function<User, UserListDTO> fn = UserListDTO::new;

        return new PageConvertDTO<>(search, fn);

    }

    public PageDTO<PostReportDTO> makeReportedPostPage(PageListDTO pageListDTO) {

        return new PageDTO<>(postRepository.reportedList(pageListDTO.getPageable()));

    }

    public PageDTO<CommentReportDTO> makeReportCommentPage(PageListDTO pageListDTO) {

        return new PageDTO<>(commentRepository.commentReports(pageListDTO.getPageable()));
    }






}
