package com.example.board.toyboard.Service;

import com.example.board.toyboard.DTO.PageResultDTO;
import com.example.board.toyboard.DTO.PostUpdateDTO;
import com.example.board.toyboard.DTO.PostWriteDTO;
import com.example.board.toyboard.DTO.SearchDTO;
import com.example.board.toyboard.Entity.Post.Post;
import com.example.board.toyboard.Entity.Post.Recommendation;
import com.example.board.toyboard.Entity.Report.Report;
import com.example.board.toyboard.Entity.User;
import com.example.board.toyboard.Entity.log.PostLog;
import com.example.board.toyboard.Exception.PostNotFoundException;
import com.example.board.toyboard.Exception.UserNotFoundException;
import com.example.board.toyboard.Repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final RecommendationRepository recommendationRepository;

    private final ReportRepository reportRepository;

    private final LogRepository logRepository;

    @Transactional(readOnly = true)
    public List<Post> findAll() {
        return postRepository.findAll();
    }

    @Transactional(readOnly = true)
    public PageResultDTO makePageResult(Pageable pageable, SearchDTO searchDTO, String postType) {


        Page<Post> posts = postRepository.search(searchDTO, pageable, postType);
        log.info("posts = {}", posts);

        return new PageResultDTO(posts);

    }

    @Transactional(readOnly = true)
    public Post findById(Long postId) {

        return postRepository.findById(postId).orElseThrow(PostNotFoundException::new);

    }

    public void upHit(Long postId) {

        postRepository.findById(postId).ifPresent(Post::addHits);

    }


    public Long write(PostWriteDTO dto, User loginUser) {

        Post post = Post.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .hits(0)
                .postType(dto.getPostType())
                .recommendedNumber(0)
                .build();

        post.setWriter(loginUser);


        Post save = postRepository.save(post);

        logRepository.save(new PostLog(loginUser, post, "게시물을 작성했습니다."));

        return save.getId();

    }

    public void update(Long postId, PostUpdateDTO dto) {

        Post post = findById(postId);

        post.updateTitle(dto.getTitle());
        post.updateContent(dto.getContent());
        post.updatePostType(dto.getPostType());

    }

    public int recommend(Long postId, String nickname) {
        Post post = postRepository.findById(postId).orElseThrow(PostNotFoundException::new);
        User user = userRepository.findByNickname(nickname).orElseThrow(UserNotFoundException::new);

        log.info("number1 = {}", post.getRecommendedNumber());

        Optional<Recommendation> check = recommendationRepository.findByUserAndPost(user, post);


        if (check.isPresent()) {
            recommendationRepository.delete(check.get());

            post.subRecommendedNumber();

        } else {

            recommendationRepository.save(
                    Recommendation.builder()
                            .user(user)
                            .post(post)
                            .build()
            );

            post.addRecommendedNumber();

        }

        log.info("number2 = {}", post.getRecommendedNumber());

        return post.getRecommendedNumber();




    }



}
