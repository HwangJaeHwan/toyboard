package com.example.board.toyboard.service;

import com.example.board.toyboard.DTO.*;
import com.example.board.toyboard.entity.post.Post;
import com.example.board.toyboard.entity.post.PostDocument;
import com.example.board.toyboard.entity.post.PostType;
import com.example.board.toyboard.entity.User;
import com.example.board.toyboard.entity.UserType;
import com.example.board.toyboard.entity.log.LogType;
import com.example.board.toyboard.exception.PostNotFoundException;
import com.example.board.toyboard.repository.LogRepository;
import com.example.board.toyboard.repository.UserRepository;
import com.example.board.toyboard.repository.post.PostDocumentRepository;
import com.example.board.toyboard.repository.post.PostRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;


@SpringBootTest
@Transactional
class PostServiceTest {


    @Autowired
    PostService postService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    LogRepository logRepository;

    @MockBean
    PostDocumentRepository postDocumentRepository;

    @MockBean
    PostRedisService redisService;

    @MockBean
    private ElasticsearchOperations operations;


    @AfterEach
    void clean() {
        logRepository.deleteAllInBatch();
        postRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();

    }

    @DisplayName("게시글 타입이 FREE인 게시물 리스트를 가져온다.")
    @Test
    void getPostsByTypeFree_success() {

        // given
        User user = userRepository.save(
                User.builder()
                        .loginId("testID")
                        .password("password")
                        .nickname("tester")
                        .userType(UserType.USER)
                        .email("test@test.com")
                        .build()
        );

        savePosts(user, PostType.FREE);

        PageRequest pageRequest = PageRequest.of(0, 10);

        // when
        PageDTO<PostListDTO> result = postService.makePageResult(pageRequest, PostType.FREE);

        // then
        assertThat(result.getTotalPage()).isEqualTo(10);
        assertThat(result.getDtoList()).hasSize(10);

        assertThat(result.getDtoList())
                .extracting(PostListDTO::getCommentNum)
                .containsOnly(0L);
        assertThat(result.getDtoList())
                .extracting(PostListDTO::getRecommendedNumber)
                .containsOnly(0L);
    }

    @DisplayName("게시글 타입이 INFO인 게시물 리스트를 가져온다.")
    @Test
    void getPostsByTypeInfo_success() {

        // given
        User user = userRepository.save(
                User.builder()
                        .loginId("testID")
                        .password("password")
                        .nickname("tester")
                        .userType(UserType.USER)
                        .email("test@test.com")
                        .build()
        );

        savePosts(user,PostType.INFO);

        PageRequest pageRequest = PageRequest.of(0, 10);

        // when
        PageDTO<PostListDTO> result = postService.makePageResult(pageRequest, PostType.INFO);

        // then
        assertThat(result.getTotalPage()).isEqualTo(10);
        assertThat(result.getDtoList()).hasSize(10);

        assertThat(result.getDtoList())
                .extracting(PostListDTO::getCommentNum)
                .containsOnly(0L);
        assertThat(result.getDtoList())
                .extracting(PostListDTO::getRecommendedNumber)
                .containsOnly(0L);
    }

    @DisplayName("게시글 타입이 QNA인 게시물 리스트를 가져온다.")
    @Test
    void getPostsByTypeQna_success() {

        // given
        User user = userRepository.save(
                User.builder()
                        .loginId("testID")
                        .password("password")
                        .nickname("tester")
                        .userType(UserType.USER)
                        .email("test@test.com")
                        .build()
        );

        savePosts(user,PostType.QNA);

        PageRequest pageRequest = PageRequest.of(0, 10);

        // when
        PageDTO<PostListDTO> result = postService.makePageResult(pageRequest, PostType.QNA);

        // then
        assertThat(result.getTotalPage()).isEqualTo(10);
        assertThat(result.getDtoList()).hasSize(10);

        assertThat(result.getDtoList())
                .extracting(PostListDTO::getCommentNum)
                .containsOnly(0L);
        assertThat(result.getDtoList())
                .extracting(PostListDTO::getRecommendedNumber)
                .containsOnly(0L);
    }

    @DisplayName("게시글 타입이 NOTICE인 게시물 리스트를 가져온다.")
    @Test
    void getPostsByTypeNotice_success() {

        // given
        User user = userRepository.save(
                User.builder()
                        .loginId("testID")
                        .password("password")
                        .nickname("tester")
                        .userType(UserType.USER)
                        .email("test@test.com")
                        .build()
        );

        savePosts(user,PostType.NOTICE);

        PageRequest pageRequest = PageRequest.of(0, 10);

        // when
        PageDTO<PostListDTO> result = postService.makePageResult(pageRequest, PostType.NOTICE);

        // then
        assertThat(result.getTotalPage()).isEqualTo(10);
        assertThat(result.getDtoList()).hasSize(10);

        assertThat(result.getDtoList())
                .extracting(PostListDTO::getCommentNum)
                .containsOnly(0L);
        assertThat(result.getDtoList())
                .extracting(PostListDTO::getRecommendedNumber)
                .containsOnly(0L);
    }

    @DisplayName("게시글을 조회수 순으로 정렬해서 가져온다.")
    @Test
    void getPostsSortedByHits_success() {

        // given
        User user = userRepository.save(
                User.builder()
                        .loginId("testID")
                        .password("password")
                        .nickname("tester")
                        .userType(UserType.USER)
                        .email("test@test.com")
                        .build()
        );

        List<Post> posts = new ArrayList<>();

        IntStream.rangeClosed(1, 100).forEach(i -> {
            posts.add(
                    Post.builder()
                            .title("test" + i)
                            .content("test " + i)
                            .postType(PostType.FREE)
                            .hits(i)
                            .user(user)
                            .build()
            );
        });

        postRepository.saveAll(posts);

        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("hits"));


        // when
        PageDTO<PostListDTO> result = postService.makePageResult(pageRequest, PostType.FREE);

        //then
        assertThat(result.getTotalPage()).isEqualTo(10);
        assertThat(result.getDtoList()).hasSize(10);


        assertThat(result.getDtoList())
                .extracting(PostListDTO::getHits)
                .containsExactly(100L, 99L, 98L, 97L, 96L, 95L, 94L, 93L, 92L, 91L);
    }


    @DisplayName("게시글을 조회하면 게시글 정보를 반환하고 조회수를 레디스에 캐싱한다.")
    @Test
    void readPost_increaseViewCountWithRedis() {

        // given
        User user = userRepository.save(
                User.builder()
                        .loginId("testID")
                        .password("password")
                        .nickname("tester")
                        .userType(UserType.USER)
                        .email("test@test.com")
                        .build()
        );

        Post post = postRepository.save(
                Post.builder()
                        .title("test title")
                        .content("test content")
                        .postType(PostType.FREE)
                        .hits(0)
                        .user(user)
                        .build()
        );

        // when
        PostReadDTO result = postService.read(post.getId(), user.getId());

        // then
        assertThat(result.getId()).isEqualTo(post.getId());
        assertThat(result.getTitle()).isEqualTo("test title");
        assertThat(result.getContent()).isEqualTo("test content");
        assertThat(result.getNickname()).isEqualTo(user.getNickname());
        assertThat(result.getViewCount()).isEqualTo(0L);
        assertThat(result.getRecommendedNumber()).isEqualTo(0L);
        assertThat(result.getCreateTime()).isNotNull();

        then(redisService).should().incrementViewCount(post.getId(), user.getId());

    }

    @DisplayName("게시판 타입별 최신 게시글 5개씩 가져온다.")
    @Test
    void getLatestPosts_returnLatest5PostsPerType_success() {

        // given
        User user = userRepository.save(
                User.builder()
                        .loginId("testID")
                        .password("password")
                        .nickname("tester")
                        .userType(UserType.USER)
                        .email("test@test.com")
                        .build()
        );

        savePostsWithPrefixAndCount(user, PostType.FREE, "FREE", 0);
        savePostsWithPrefixAndCount(user, PostType.INFO, "INFO", 5);
        savePostsWithPrefixAndCount(user, PostType.QNA, "QNA", 9);
        savePostsWithPrefixAndCount(user, PostType.NOTICE, "NOTICE", 11);

        // when
        LatestPosts result = postService.getLatestPosts();

        // then
        assertThat(result.getFreeList()).hasSize(0);
        assertThat(result.getInfoList()).hasSize(5);
        assertThat(result.getQnaList()).hasSize(5);
        assertThat(result.getNoticeList()).hasSize(5);


        assertThat(result.getInfoList())
                .extracting(PostTitle::getTitle)
                .allMatch(title -> title.startsWith("INFO"));

        assertThat(result.getQnaList())
                .extracting(PostTitle::getTitle)
                .allMatch(title -> title.startsWith("QNA"));

        assertThat(result.getNoticeList())
                .extracting(PostTitle::getTitle)
                .allMatch(title -> title.startsWith("NOTICE"));
    }


    private void savePostsWithPrefixAndCount(User user, PostType postType, String prefix, int count) {
        List<Post> posts = new ArrayList<>();

        IntStream.rangeClosed(1, count).forEach(i -> {
            posts.add(
                    Post.builder()
                            .title(prefix + i)
                            .content(prefix + " content " + i)
                            .postType(postType)
                            .hits(0)
                            .user(user)
                            .build()
            );
        });

        postRepository.saveAll(posts);
    }



    private void savePosts(User user, PostType type) {

        List<Post> posts = new ArrayList<>();

        IntStream.rangeClosed(1, 100).forEach(i -> posts.add(
                Post.builder()
                        .title(type.name() + i)
                        .content("test " + type.name() + i)
                        .postType(type)
                        .hits(0)
                        .user(user)
                        .build()
        ));

        postRepository.saveAll(posts);

    }


    @DisplayName("게시글을 작성하면 게시글 저장 후 로그를 저장하고, ES, Redis에 반영한다.")
    @Test
    void writePost_savePostA_success() {

        // given
        User user = userRepository.save(
                User.builder()
                        .loginId("testID")
                        .password("password")
                        .nickname("tester")
                        .userType(UserType.USER)
                        .email("test@test.com")
                        .build()
        );


        PostWriteDTO dto = PostWriteDTO.builder()
                .title("게시글 제목")
                .content("게시글 내용")
                .postType(PostType.FREE)
                .build();

        // when
        Long postId = postService.write(dto, user.getId());

        // then
        assertThat(postId).isNotNull();

        Post savedPost = postRepository.findById(postId).orElseThrow();
        assertThat(savedPost.getTitle()).isEqualTo("게시글 제목");
        assertThat(savedPost.getContent()).isEqualTo("게시글 내용");
        assertThat(savedPost.getPostType()).isEqualTo(PostType.FREE);
        assertThat(savedPost.getUser().getId()).isEqualTo(user.getId());

        assertThat(logRepository.findAll())
                .hasSize(1)
                .allSatisfy(log -> {
                    assertThat(log.getUser().getId()).isEqualTo(user.getId());
                    assertThat(log.getPost().getId()).isEqualTo(postId);
                    assertThat(log.getLogType()).isEqualTo(LogType.POST);
                });

        then(postDocumentRepository).should().save(any(PostDocument.class));
        then(redisService).should().saveLatestPost(PostType.FREE, postId);
    }

    @DisplayName("작성자는 게시글을 삭제할 수 있다.")
    @Test
    void deletePost_byWriter_success() {

        // given
        User user = userRepository.save(
                User.builder()
                        .loginId("testID")
                        .password("password")
                        .nickname("tester")
                        .userType(UserType.USER)
                        .email("test@test.com")
                        .build()
        );

        Post post = postRepository.save(
                Post.builder()
                        .title("제목")
                        .content("내용")
                        .postType(PostType.FREE)
                        .hits(0)
                        .user(user)
                        .build()
        );

        // when
        postService.delete(user.getId(), UserType.USER, post.getId());

        // then
        assertThat(postRepository.findById(post.getId())).isEmpty();
        then(postDocumentRepository).should().deleteById(post.getId().toString());
    }

    @DisplayName("존재하지 않는 게시글 삭제 시 예외가 발생한다.")
    @Test
    void deletePost_notfound_fail() {

        // given
        Long notExistPostId = 9999L;

        // when & then
        assertThatThrownBy(() -> postService.delete(1L, UserType.USER, notExistPostId))
                .isInstanceOf(PostNotFoundException.class);
    }

    @DisplayName("작성자도 관리자도 아니면 게시글을 삭제할 수 없다.")
    @Test
    void deletePost_notWriterAndAdmin_fail() {

        // given
        User writer = userRepository.save(
                User.builder()
                        .loginId("writer")
                        .password("password")
                        .nickname("writer")
                        .userType(UserType.USER)
                        .email("writer@test.com")
                        .build()
        );

        User otherUser = userRepository.save(
                User.builder()
                        .loginId("other")
                        .password("password")
                        .nickname("other")
                        .userType(UserType.USER)
                        .email("other@test.com")
                        .build()
        );

        Post post = postRepository.save(
                Post.builder()
                        .title("제목")
                        .content("내용")
                        .postType(PostType.FREE)
                        .hits(0)
                        .user(writer)
                        .build()
        );

        // when & then
        assertThatThrownBy(() -> postService.delete(otherUser.getId(), UserType.USER, post.getId()))
                .isInstanceOf(RuntimeException.class);
    }

    @DisplayName("관리자는 본인 글이 아니어도 게시글을 삭제할 수 있다.")
    @Test
    void deletePost_byAdmin_success() {

        // given
        User writer = userRepository.save(
                User.builder()
                        .loginId("writer")
                        .password("password")
                        .nickname("writer")
                        .userType(UserType.USER)
                        .email("writer@test.com")
                        .build()
        );

        Post post = postRepository.save(
                Post.builder()
                        .title("제목")
                        .content("내용")
                        .postType(PostType.FREE)
                        .hits(0)
                        .user(writer)
                        .build()
        );

        // when
        postService.delete(999L, UserType.ADMIN, post.getId());

        // then
        assertThat(postRepository.findById(post.getId())).isEmpty();
        then(postDocumentRepository).should().deleteById(post.getId().toString());
    }


    @DisplayName("TITLE 검색 시 title 하이라이트가 존재하면 검색 결과를 Page<PostListDTO>로 반환한다")
    @Test
    void search_title_withTitleHighlight() {
        // given
        User writer = userRepository.save(
                User.builder()
                        .loginId("writer")
                        .password("password")
                        .nickname("writer")
                        .userType(UserType.USER)
                        .email("writer@test.com")
                        .build()
        );

        Post post = postRepository.save(
                Post.builder()
                        .title("스프링 제목")
                        .content("스프링 내용")
                        .postType(PostType.FREE)
                        .hits(10)
                        .user(writer)
                        .build()
        );




        Pageable pageable = PageRequest.of(0, 10);

        SearchDTO searchDTO = new SearchDTO();
        searchDTO.setType(SearchType.TITLE);
        searchDTO.setContent("스프링");

        PostType postType = PostType.FREE;

        PostDocument postDocument = PostDocument.builder()
                .id(post.getId().toString())
                .title("스프링 제목")
                .nickname("writer")
                .createdTime(post.getCreatedTime())
                .build();

//        SearchInfo searchInfo = mock(SearchInfo.class);
//        given(searchInfo.getId()).willReturn(1L);
//        given(searchInfo.getHits()).willReturn(123L);
//        given(searchInfo.getRecommendCount()).willReturn(7L);
//        given(searchInfo.getCreateAt()).willReturn(LocalDateTime.of(2026, 3, 31, 21, 0));

        SearchHit<PostDocument> searchHit = mock(SearchHit.class);
        given(searchHit.getContent()).willReturn(postDocument);
        given(searchHit.getHighlightField("title")).willReturn(List.of("<b>스프링</b> 제목"));
        given(searchHit.getHighlightField("title.ngram")).willReturn(Collections.emptyList());

        SearchHits<PostDocument> searchHits = mock(SearchHits.class);
        given(searchHits.getSearchHits()).willReturn(List.of(searchHit));
        given(searchHits.getTotalHits()).willReturn(1L);

        given(operations.search(any(Query.class), eq(PostDocument.class)))
                .willReturn(searchHits);


        // when
        Page<PostListDTO> result = postService.search(pageable, searchDTO, postType);

        // then
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).hasSize(1);

        PostListDTO dto = result.getContent().get(0);
        assertThat(dto.getId()).isEqualTo(post.getId());

        assertThat(dto.getTitle()).isEqualTo("<b>스프링</b> 제목");

        assertThat(dto.getNickname()).isEqualTo("writer");
        assertThat(dto.getHits()).isEqualTo(10);
        assertThat(dto.getRecommendedNumber()).isEqualTo(0L);
        assertThat(dto.getCreatedTime()).isEqualTo(post.getCreatedTime());

        then(operations).should().search(any(Query.class), eq(PostDocument.class));

    }

    @DisplayName("TITLE 검색 시 title 하이라이트가 없고 title.ngram 하이라이트가 존재하면 해당 하이라이트 제목을 반환한다")
    @Test
    void search_title_withNgramHighlight() {
        // given
        User writer = userRepository.save(
                User.builder()
                        .loginId("writer")
                        .password("password")
                        .nickname("writer")
                        .userType(UserType.USER)
                        .email("writer@test.com")
                        .build()
        );

        Post post = postRepository.save(
                Post.builder()
                        .title("스프링 제목")
                        .content("스프링 내용")
                        .postType(PostType.FREE)
                        .hits(10)
                        .user(writer)
                        .build()
        );

        Pageable pageable = PageRequest.of(0, 10);

        SearchDTO searchDTO = new SearchDTO();
        searchDTO.setType(SearchType.TITLE);
        searchDTO.setContent("스프링");

        PostDocument postDocument = PostDocument.builder()
                .id(post.getId().toString())
                .title("스프링 제목")
                .nickname("writer")
                .createdTime(post.getCreatedTime())
                .build();

        SearchHit<PostDocument> searchHit = mock(SearchHit.class);
        given(searchHit.getContent()).willReturn(postDocument);
        given(searchHit.getHighlightField("title")).willReturn(Collections.emptyList());
        given(searchHit.getHighlightField("title.ngram")).willReturn(List.of("<b>스프링</b> 제목"));

        SearchHits<PostDocument> searchHits = mock(SearchHits.class);
        given(searchHits.getSearchHits()).willReturn(List.of(searchHit));
        given(searchHits.getTotalHits()).willReturn(1L);

        given(operations.search(any(Query.class), eq(PostDocument.class)))
                .willReturn(searchHits);

        // when
        Page<PostListDTO> result = postService.search(pageable, searchDTO, PostType.FREE);

        // then
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).hasSize(1);

        PostListDTO dto = result.getContent().get(0);
        assertThat(dto.getId()).isEqualTo(post.getId());
        assertThat(dto.getTitle()).isEqualTo("<b>스프링</b> 제목");
        assertThat(dto.getNickname()).isEqualTo("writer");
        assertThat(dto.getHits()).isEqualTo(10);
        assertThat(dto.getRecommendedNumber()).isEqualTo(0L);
        assertThat(dto.getCreatedTime()).isEqualTo(post.getCreatedTime());

        then(operations).should().search(any(Query.class), eq(PostDocument.class));
    }

    @DisplayName("TITLE 검색 시 title 하이라이트와 title.ngram 하이라이트가 모두 없으면 원본 제목을 반환한다")
    @Test
    void search_title_withoutHighlight() {
        // given
        User writer = userRepository.save(
                User.builder()
                        .loginId("writer")
                        .password("password")
                        .nickname("writer")
                        .userType(UserType.USER)
                        .email("writer@test.com")
                        .build()
        );

        Post post = postRepository.save(
                Post.builder()
                        .title("스프링 제목")
                        .content("스프링 내용")
                        .postType(PostType.FREE)
                        .hits(10)
                        .user(writer)
                        .build()
        );

        Pageable pageable = PageRequest.of(0, 10);

        SearchDTO searchDTO = new SearchDTO();
        searchDTO.setType(SearchType.TITLE);
        searchDTO.setContent("스프링");

        PostDocument postDocument = PostDocument.builder()
                .id(post.getId().toString())
                .title("스프링 제목")
                .nickname("writer")
                .createdTime(post.getCreatedTime())
                .build();

        SearchHit<PostDocument> searchHit = mock(SearchHit.class);
        given(searchHit.getContent()).willReturn(postDocument);
        given(searchHit.getHighlightField("title")).willReturn(Collections.emptyList());
        given(searchHit.getHighlightField("title.ngram")).willReturn(Collections.emptyList());

        SearchHits<PostDocument> searchHits = mock(SearchHits.class);
        given(searchHits.getSearchHits()).willReturn(List.of(searchHit));
        given(searchHits.getTotalHits()).willReturn(1L);

        given(operations.search(any(Query.class), eq(PostDocument.class)))
                .willReturn(searchHits);

        // when
        Page<PostListDTO> result = postService.search(pageable, searchDTO, PostType.FREE);

        // then
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).hasSize(1);

        PostListDTO dto = result.getContent().get(0);
        assertThat(dto.getId()).isEqualTo(post.getId());
        assertThat(dto.getTitle()).isEqualTo("스프링 제목");
        assertThat(dto.getNickname()).isEqualTo("writer");
        assertThat(dto.getHits()).isEqualTo(10);
        assertThat(dto.getRecommendedNumber()).isEqualTo(0L);
        assertThat(dto.getCreatedTime()).isEqualTo(postDocument.getCreatedTime());

        then(operations).should().search(any(Query.class), eq(PostDocument.class));
    }

    @DisplayName("검색 결과가 없으면 빈 페이지를 반환한다")
    @Test
    void search_emptyResult() {
        // given
        Pageable pageable = PageRequest.of(0, 10);

        SearchDTO searchDTO = new SearchDTO();
        searchDTO.setType(SearchType.TITLE);
        searchDTO.setContent("없는검색어");

        SearchHits<PostDocument> searchHits = mock(SearchHits.class);
        given(searchHits.getSearchHits()).willReturn(Collections.emptyList());
        given(searchHits.getTotalHits()).willReturn(0L);

        given(operations.search(any(Query.class), eq(PostDocument.class)))
                .willReturn(searchHits);

        // when
        Page<PostListDTO> result = postService.search(pageable, searchDTO, PostType.FREE);

        // then
        assertThat(result.getTotalElements()).isEqualTo(0);
        assertThat(result.getContent()).isEmpty();

        then(operations).should().search(any(Query.class), eq(PostDocument.class));
    }

    @DisplayName("검색어가 비어 있으면 타입 필터만 적용하여 검색하고 결과를 반환한다")
    @Test
    void search_blankContent() {
        // given
        User writer = userRepository.save(
                User.builder()
                        .loginId("writer")
                        .password("password")
                        .nickname("writer")
                        .userType(UserType.USER)
                        .email("writer@test.com")
                        .build()
        );

        Post post = postRepository.save(
                Post.builder()
                        .title("전체 조회 제목")
                        .content("내용")
                        .postType(PostType.FREE)
                        .hits(5)
                        .user(writer)
                        .build()
        );

        Pageable pageable = PageRequest.of(0, 10);

        SearchDTO searchDTO = new SearchDTO();
        searchDTO.setType(SearchType.TITLE);
        searchDTO.setContent("   ");

        PostDocument postDocument = PostDocument.builder()
                .id(post.getId().toString())
                .title("전체 조회 제목")
                .nickname("writer")
                .createdTime(post.getCreatedTime())
                .build();

        SearchHit<PostDocument> searchHit = mock(SearchHit.class);
        given(searchHit.getContent()).willReturn(postDocument);
        given(searchHit.getHighlightField("title")).willReturn(Collections.emptyList());
        given(searchHit.getHighlightField("title.ngram")).willReturn(Collections.emptyList());

        SearchHits<PostDocument> searchHits = mock(SearchHits.class);
        given(searchHits.getSearchHits()).willReturn(List.of(searchHit));
        given(searchHits.getTotalHits()).willReturn(1L);

        given(operations.search(any(Query.class), eq(PostDocument.class)))
                .willReturn(searchHits);

        // when
        Page<PostListDTO> result = postService.search(pageable, searchDTO, PostType.FREE);

        // then
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).hasSize(1);

        PostListDTO dto = result.getContent().get(0);
        assertThat(dto.getId()).isEqualTo(post.getId());
        assertThat(dto.getTitle()).isEqualTo("전체 조회 제목");
        assertThat(dto.getNickname()).isEqualTo("writer");
        assertThat(dto.getHits()).isEqualTo(5);
        assertThat(dto.getRecommendedNumber()).isEqualTo(0L);
        assertThat(dto.getCreatedTime()).isEqualTo(post.getCreatedTime());

        then(operations).should().search(any(Query.class), eq(PostDocument.class));
    }

    @DisplayName("CONTENT 검색 시 title 하이라이트가 없으면 원본 제목을 반환한다")
    @Test
    void search_content_originalTitleReturned() {
        // given
        User writer = userRepository.save(
                User.builder()
                        .loginId("writer")
                        .password("password")
                        .nickname("writer")
                        .userType(UserType.USER)
                        .email("writer@test.com")
                        .build()
        );

        Post post = postRepository.save(
                Post.builder()
                        .title("원본 제목")
                        .content("스프링 내용")
                        .postType(PostType.FREE)
                        .hits(8)
                        .user(writer)
                        .build()
        );

        Pageable pageable = PageRequest.of(0, 10);

        SearchDTO searchDTO = new SearchDTO();
        searchDTO.setType(SearchType.CONTENT);
        searchDTO.setContent("스프링");

        PostDocument postDocument = PostDocument.builder()
                .id(post.getId().toString())
                .title("원본 제목")
                .nickname("writer")
                .createdTime(post.getCreatedTime())
                .build();

        SearchHit<PostDocument> searchHit = mock(SearchHit.class);
        given(searchHit.getContent()).willReturn(postDocument);
        given(searchHit.getHighlightField("title")).willReturn(Collections.emptyList());
        given(searchHit.getHighlightField("title.ngram")).willReturn(Collections.emptyList());

        SearchHits<PostDocument> searchHits = mock(SearchHits.class);
        given(searchHits.getSearchHits()).willReturn(List.of(searchHit));
        given(searchHits.getTotalHits()).willReturn(1L);

        given(operations.search(any(Query.class), eq(PostDocument.class)))
                .willReturn(searchHits);

        // when
        Page<PostListDTO> result = postService.search(pageable, searchDTO, PostType.FREE);

        // then
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).hasSize(1);

        PostListDTO dto = result.getContent().get(0);
        assertThat(dto.getId()).isEqualTo(post.getId());
        assertThat(dto.getTitle()).isEqualTo("원본 제목");
        assertThat(dto.getNickname()).isEqualTo("writer");
        assertThat(dto.getHits()).isEqualTo(8);
        assertThat(dto.getRecommendedNumber()).isEqualTo(0L);
        assertThat(dto.getCreatedTime()).isEqualTo(post.getCreatedTime());

        then(operations).should().search(any(Query.class), eq(PostDocument.class));
    }






}