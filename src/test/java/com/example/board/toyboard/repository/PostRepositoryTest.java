package com.example.board.toyboard.repository;

import com.example.board.toyboard.DTO.*;
import com.example.board.toyboard.entity.Comment;
import com.example.board.toyboard.entity.post.Post;
import com.example.board.toyboard.entity.post.PostType;
import com.example.board.toyboard.entity.post.Recommendation;
import com.example.board.toyboard.entity.report.PostReport;
import com.example.board.toyboard.entity.User;
import com.example.board.toyboard.entity.UserType;
import com.example.board.toyboard.repository.post.PostDocumentRepository;
import com.example.board.toyboard.repository.post.PostRepository;
import com.example.board.toyboard.repository.report.PostReportRepository;
import com.example.board.toyboard.service.PostRedisService;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class PostRepositoryTest {

    @Autowired
    PostRepository postRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    RecommendationRepository recommendationRepository;

    @Autowired
    PostReportRepository postReportRepository;

    @Autowired
    EntityManager em;

    @MockBean
    PostDocumentRepository postDocumentRepository;

    @MockBean
    PostRedisService redisService;

    @DisplayName("게시글 타입별 최신 게시글을 limit 개수만큼 조회한다")
    @Test
    void findLatestPostsByType() {
        // given
        User user = saveUser("user1");

        Post post1 = savePost(user, "제목1", PostType.FREE, 0);
        Post post2 = savePost(user, "제목2", PostType.FREE, 0);
        Post post3 = savePost(user, "제목3", PostType.FREE, 0);
        savePost(user, "정보글", PostType.INFO, 0);

        em.flush();
        em.clear();

        // when
        List<PostTitle> result = postRepository.findLatestPostsByType(PostType.FREE, 2);

        // then
        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(PostTitle::getTitle)
                .containsExactly("제목3", "제목2");
    }

    @DisplayName("게시글 목록을 게시글 타입 기준으로 조회한다")
    @Test
    void findPosts() {
        // given
        User user = saveUser("user1");

        Post post1 = savePost(user, "제목1", PostType.FREE, 10);
        Post post2 = savePost(user, "제목2", PostType.FREE, 20);
        savePost(user, "정보글", PostType.INFO, 30);

        Pageable pageable = PageRequest.of(0, 10);

        em.flush();
        em.clear();

        // when
        Page<PostListDTO> result = postRepository.findPosts(pageable, PostType.FREE);

        // then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(2);

        assertThat(result.getContent())
                .extracting(PostListDTO::getTitle)
                .containsExactly("제목2", "제목1");
    }

    @DisplayName("추천 수 기준으로 게시글 목록을 조회한다")
    @Test
    void findPostsOrderByRecommendation() {
        // given
        User writer = saveUser("writer");
        User user1 = saveUser("user1");
        User user2 = saveUser("user2");

        Post post1 = savePost(writer, "추천 1개", PostType.FREE, 0);
        Post post2 = savePost(writer, "추천 2개", PostType.FREE, 0);

        saveRecommendation(user1, post1);

        saveRecommendation(user1, post2);
        saveRecommendation(user2, post2);

        Pageable pageable = PageRequest.of(
                0,
                10,
                Sort.by(Sort.Direction.DESC, "recommendedNumber")
        );

        em.flush();
        em.clear();

        // when
        Page<PostListDTO> result =
                postRepository.findPostsOrderByRecommendation(pageable, PostType.FREE);

        // then
        assertThat(result.getContent()).hasSize(2);

        assertThat(result.getContent())
                .extracting(PostListDTO::getTitle)
                .containsExactly("추천 2개", "추천 1개");

        assertThat(result.getContent())
                .extracting(PostListDTO::getRecommendedNumber)
                .containsExactly(2L, 1L);
    }

    @DisplayName("게시글 ID 목록으로 댓글 수를 조회한다")
    @Test
    void countCommentsByPostIds() {
        // given
        User user = saveUser("user1");

        Post post1 = savePost(user, "제목1", PostType.FREE, 0);
        Post post2 = savePost(user, "제목2", PostType.FREE, 0);

        saveComment(user, post1, "댓글1");
        saveComment(user, post1, "댓글2");
        saveComment(user, post2, "댓글3");

        em.flush();
        em.clear();

        // when
        Map<Long, Long> result =
                postRepository.countCommentsByPostIds(List.of(post1.getId(), post2.getId()));

        // then
        assertThat(result.get(post1.getId())).isEqualTo(2L);
        assertThat(result.get(post2.getId())).isEqualTo(1L);
    }

    @DisplayName("게시글 ID 목록으로 추천 수를 조회한다")
    @Test
    void countRecommendationsByPostIds() {
        // given
        User writer = saveUser("writer");
        User user1 = saveUser("user1");
        User user2 = saveUser("user2");

        Post post1 = savePost(writer, "제목1", PostType.FREE, 0);
        Post post2 = savePost(writer, "제목2", PostType.FREE, 0);

        saveRecommendation(user1, post1);

        saveRecommendation(user1, post2);
        saveRecommendation(user2, post2);

        em.flush();
        em.clear();

        // when
        Map<Long, Long> result =
                postRepository.countRecommendationsByPostIds(List.of(post1.getId(), post2.getId()));

        // then
        assertThat(result.get(post1.getId())).isEqualTo(1L);
        assertThat(result.get(post2.getId())).isEqualTo(2L);
    }

    @DisplayName("신고 수가 10개 이상인 게시글 목록을 조회한다")
    @Test
    void reportedList() {
        // given
        User writer = saveUser("writer");
        Post post = savePost(writer, "신고글", PostType.FREE, 0);

        for (int i = 0; i < 10; i++) {
            User reporter = saveUser("reporter" + i);
            savePostReport(reporter, post);
        }

        Pageable pageable = PageRequest.of(0, 10);

        em.flush();
        em.clear();

        // when
        Page<PostReportDTO> result = postRepository.reportedList(pageable);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("신고글");
        assertThat(result.getContent().get(0).getReposts()).isEqualTo(10L);
    }



    @DisplayName("검색 결과에 필요한 게시글 부가 정보를 Map으로 조회한다")
    @Test
    void search() {
        // given
        User writer = saveUser("writer");
        User user1 = saveUser("user1");
        User user2 = saveUser("user2");

        Post post = savePost(writer, "검색글", PostType.FREE, 7);

        saveRecommendation(user1, post);
        saveRecommendation(user2, post);

        em.flush();
        em.clear();

        // when
        Map<Long, SearchInfo> result = postRepository.search(List.of(post.getId()));

        // then
        SearchInfo searchInfo = result.get(post.getId());

        assertThat(searchInfo).isNotNull();
        assertThat(searchInfo.getId()).isEqualTo(post.getId());
        assertThat(searchInfo.getHits()).isEqualTo(7);
        assertThat(searchInfo.getRecommendCount()).isEqualTo(2L);
    }

    @DisplayName("게시글 조회수를 벌크 업데이트한다")
    @Test
    void bulkUpdateViewCounts() {
        // given
        User user = saveUser("user1");

        Post post1 = savePost(user, "제목1", PostType.FREE, 10);
        Post post2 = savePost(user, "제목2", PostType.FREE, 20);

        em.flush();
        em.clear();

        // when
        postRepository.bulkUpdateViewCounts(
                Map.of(
                        post1.getId(), 3,
                        post2.getId(), 5
                )
        );

        em.flush();
        em.clear();

        // then
        Post findPost1 = postRepository.findById(post1.getId()).orElseThrow();
        Post findPost2 = postRepository.findById(post2.getId()).orElseThrow();

        assertThat(findPost1.getHits()).isEqualTo(13);
        assertThat(findPost2.getHits()).isEqualTo(25);
    }

    private User saveUser(String loginId) {
        return userRepository.save(
                User.builder()
                        .loginId(loginId)
                        .password("1234")
                        .nickname(loginId)
                        .email(loginId + "@test.com")
                        .userType(UserType.USER)
                        .build()
        );
    }

    private Post savePost(User user, String title, PostType postType, int hits) {
        return postRepository.save(
                Post.builder()
                        .title(title)
                        .content("내용")
                        .postType(postType)
                        .hits(hits)
                        .user(user)
                        .build()
        );
    }

    private Comment saveComment(User user, Post post, String content) {
        return commentRepository.save(
                Comment.builder()
                        .user(user)
                        .post(post)
                        .comment(content)
                        .build()
        );
    }

    private Recommendation saveRecommendation(User user, Post post) {
        return recommendationRepository.save(
                new Recommendation(user, post)
        );
    }

    private PostReport savePostReport(User user, Post post) {
        return postReportRepository.save(
                new PostReport(user, post)
        );
    }

}