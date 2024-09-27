package com.example.board.toyboard.Service;

import com.example.board.toyboard.DTO.*;
import com.example.board.toyboard.Entity.Post.Post;
import com.example.board.toyboard.Entity.Post.Recommendation;
import com.example.board.toyboard.Entity.User;
import com.example.board.toyboard.Entity.log.Log;
import com.example.board.toyboard.Entity.log.LogType;
import com.example.board.toyboard.Exception.PostNotFoundException;
import com.example.board.toyboard.Exception.UserNotFoundException;
import com.example.board.toyboard.Repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    private final CommentRepository commentRepository;

    private final LogRepository logRepository;

    @Transactional(readOnly = true)
    public List<Post> findAll() {
        return postRepository.findAll();
    }

    @Transactional(readOnly = true)
    public PageDTO<PostListDTO> makePageResult(Pageable pageable, SearchDTO searchDTO, String postType) {

        return new PageDTO<>(postRepository.search(searchDTO, pageable, postType));

    }

    @Transactional(readOnly = true)
    public Post findById(Long postId) {

        return postRepository.findById(postId).orElseThrow(PostNotFoundException::new);

    }

    @Transactional(readOnly = true)
    public Post findByIdWithUser(Long postId) {
        return postRepository.findByIdWithUser(postId).orElseThrow(PostNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public PostReadDTO read(Long postId) {

        return postRepository.postRead(postId);
    }

    public LatestPosts getLatestPosts() {

        return postRepository.getLatestPosts();

    }

    public void getPopularPosts() {

    }



    public Long write(PostWriteDTO dto, User loginUser) {

        Post post = Post.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .hits(0)
                .postType(dto.getPostType())
                .build();

        post.setWriter(loginUser);


        Post save = postRepository.save(post);

        logRepository.save(new Log(loginUser, post, LogType.POST));

        return save.getId();

    }

    public void delete(Post post) {

        logRepository.deleteAllByPost(post);
        commentRepository.deleteAllByPost(post);
        recommendationRepository.deleteAllByPost(post);
        postRepository.delete(post);



    }

    public void update(Post post, PostUpdateDTO dto) {

        post.updateTitle(dto.getTitle());
        post.updateContent(dto.getContent());
        post.updatePostType(dto.getPostType());

    }

    public int recommend(Long postId, String nickname) {
        Post post = postRepository.findById(postId).orElseThrow(PostNotFoundException::new);
        User user = userRepository.findByNickname(nickname).orElseThrow(UserNotFoundException::new);


        Optional<Recommendation> check = recommendationRepository.findByUserAndPost(user, post);


        if (check.isPresent()) {
            recommendationRepository.delete(check.get());

            logRepository.findLogByUserAndPostAndLogType(user, post, LogType.RECOMMEND).ifPresent(logRepository::delete);

        } else {

            recommendationRepository.save(new Recommendation(user, post));

            logRepository.save(new Log(user, post, LogType.RECOMMEND));
        }

//        recommendationRepository


//        return post.getRecommendedNumber();

        return recommendationRepository.countAllByPost(post);



    }

    public PageDTO<PostReportDTO> postsWithReport(PageListDTO pageListDTO) {

        return new PageDTO<>(postRepository.reportedList(pageListDTO.getPageable()));
    }



}
