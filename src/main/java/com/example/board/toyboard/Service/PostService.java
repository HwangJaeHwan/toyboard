package com.example.board.toyboard.Service;

import com.example.board.toyboard.DTO.PostWriteDTO;
import com.example.board.toyboard.Entity.Post;
import com.example.board.toyboard.Entity.User;
import com.example.board.toyboard.Repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;


    @Transactional(readOnly = true)
    public List<Post> findAll() {
        return postRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Post findById(Long postId) {

        return postRepository.findById(postId).orElse(null);

    }


    public Long write(PostWriteDTO dto, User loginUser) {

        Post post = Post.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .createTime(LocalDateTime.now())
                .hits(0)
                .recommendedNumber(0)
                .build();

        post.setUser(loginUser);

        Post save = postRepository.save(post);

        return save.getId();

    }



}
