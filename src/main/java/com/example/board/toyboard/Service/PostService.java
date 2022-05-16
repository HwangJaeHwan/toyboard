package com.example.board.toyboard.Service;

import com.example.board.toyboard.DTO.PageResultDTO;
import com.example.board.toyboard.DTO.PostUpdateDTO;
import com.example.board.toyboard.DTO.PostWriteDTO;
import com.example.board.toyboard.DTO.SearchDTO;
import com.example.board.toyboard.Entity.Post.Post;
import com.example.board.toyboard.Entity.User;
import com.example.board.toyboard.Repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public PageResultDTO makePageResult(Pageable pageable, SearchDTO searchDTO) {

        Page<Post> posts = postRepository.search(searchDTO, pageable);
        log.info("posts = {}", posts);

        return new PageResultDTO(posts);

    }

    @Transactional(readOnly = true)
    public Post findById(Long postId) {

        return postRepository.findById(postId).orElse(null);

    }

    public void upHit(Long postId) {

        postRepository.findById(postId).ifPresent(Post::addHits);

    }


    public Long write(PostWriteDTO dto, User loginUser) {

        Post post = Post.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .createTime(LocalDateTime.now())
                .hits(0)
                .postType(dto.getPostType())
                .recommendedNumber(0)
                .build();

        post.setUser(loginUser);

        Post save = postRepository.save(post);

        return save.getId();

    }

    public void update(Long postId, PostUpdateDTO dto) {

        Post post = findById(postId);

        post.updateTitle(dto.getTitle());
        post.updateContent(dto.getContent());
        post.updatePostType(dto.getPostType());

    }



}
