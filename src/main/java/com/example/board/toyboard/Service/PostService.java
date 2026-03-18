package com.example.board.toyboard.Service;

import co.elastic.clients.elasticsearch._types.query_dsl.*;
import com.example.board.toyboard.DTO.*;
import com.example.board.toyboard.Entity.Post.Post;
import com.example.board.toyboard.Entity.Post.PostDocument;
import com.example.board.toyboard.Entity.Post.PostType;
import com.example.board.toyboard.Entity.Post.Recommendation;
import com.example.board.toyboard.Entity.User;
import com.example.board.toyboard.Entity.UserType;
import com.example.board.toyboard.Entity.log.Log;
import com.example.board.toyboard.Entity.log.LogType;
import com.example.board.toyboard.Exception.PostNotFoundException;
import com.example.board.toyboard.Exception.UserNotFoundException;
import com.example.board.toyboard.Repository.*;
import com.example.board.toyboard.Repository.post.PostDocumentRepository;
import com.example.board.toyboard.Repository.post.PostRepository;
import com.example.board.toyboard.session.UserSession;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.HighlightQuery;
import org.springframework.data.elasticsearch.core.query.highlight.Highlight;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightField;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

    private final PostDocumentRepository postDocumentRepository;
    private final ElasticsearchOperations operations;

    @Transactional(readOnly = true)
    public List<Post> findAll() {
        return postRepository.findAll();
    }

    @Transactional(readOnly = true)
    public PageDTO<PostListDTO> makePageResult(Pageable pageable, PostType postType) {

        long start = System.currentTimeMillis();


//        Page<PostListDTO> list1 = postRepository.list(pageable, postType);

        Page<PostListDTO> list = postRepository.findPosts(pageable, postType);

        List<Long> ids = list.stream().map(PostListDTO::getId).toList();

        Map<Long, Long> commentCountMap = postRepository.countCommentsByPostIds(ids);

        Map<Long, Long> recommendationCountMap = postRepository.countRecommendationsByPostIds(ids);

        for (PostListDTO dto : list) {
            dto.setCommentNum(commentCountMap.getOrDefault(dto.getId(), 0L));
            dto.setRecommendedNumber(recommendationCountMap.getOrDefault(dto.getId(), 0L));
        }

        long end = System.currentTimeMillis();
        log.info("조회 속도 ={} ", end - start + " ms");

        return new PageDTO<>(list);

    }

    @Transactional(readOnly = true)
    public Post findById(Long postId) {

        return postRepository.findById(postId).orElseThrow(PostNotFoundException::new);

    }

    @Transactional(readOnly = true)
    public Post findPostWithAuthCheck(Long postId, Long userId, UserType userType) {

        Post post = postRepository.findByIdWithUser(postId).orElseThrow(PostNotFoundException::new);

        authCheck(post, userId, userType);

        return post;
    }



    public PostReadDTO read(Long postId) {

        return postRepository.postRead(postId);
    }


    public LatestPosts getLatestPosts() {

        LatestPosts latestPosts = new LatestPosts();

        latestPosts.getFreeList().addAll(postRepository.findLatestPostsByType(PostType.FREE, 5));
        latestPosts.getInfoList().addAll(postRepository.findLatestPostsByType(PostType.INFO, 5));
        latestPosts.getQnaList().addAll(postRepository.findLatestPostsByType(PostType.QNA, 5));
        latestPosts.getNoticeList().addAll(postRepository.findLatestPostsByType(PostType.NOTICE, 5));

        return latestPosts;

    }

    

    public Long write(PostWriteDTO dto, Long userId) {

        log.info("아이디 = {}", userId);

        User loginUser = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        Post post = Post.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .viewCount(0)
                .postType(dto.getPostType())
                .build();

        post.setWriter(loginUser);

        Post save = postRepository.save(post);

        PostDocument postDocument = PostDocument.builder()
                .id(post.getId().toString())
                .title(post.getTitle())
                .content(post.getContent())
                .postType(post.getPostType().name())
                .writerId(loginUser.getId())
                .nickname(loginUser.getNickname())
                .createdTime(post.getCreatedTime())
                .build();


        postDocumentRepository.save(postDocument);

        logRepository.save(new Log(loginUser, post, LogType.POST));

        return save.getId();

    }

    public void delete(Long postId, Long userId, UserType userType) {

        Post post = postRepository.findByIdWithUser(postId).orElseThrow(PostNotFoundException::new);
        if (!post.getUser().getId().equals(userId) && userType != UserType.ADMIN) {
            throw new RuntimeException("삭제할 권한이 없습니다.");
        }

        logRepository.deleteAllByPost(post);
        commentRepository.deleteAllByPost(post);
        recommendationRepository.deleteAllByPost(post);
        postRepository.delete(post);

        postDocumentRepository.deleteById(post.getId().toString());


    }

    public void update(Long postId, PostUpdateDTO dto, Long userId, UserType userType) {

        Post post = postRepository.findByIdWithUser(postId).orElseThrow(PostNotFoundException::new);

        authCheck(post, userId, userType);

        post.updateTitle(dto.getTitle());
        post.updateContent(dto.getContent());
        post.updatePostType(dto.getPostType());

    }

    public int recommend(Long postId, Long userId) {
        Post post = postRepository.findById(postId).orElseThrow(PostNotFoundException::new);
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);


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


    public Page<PostListDTO> search(Pageable pageable, SearchDTO searchDTO, PostType postType) {

        log.info("서치 ={}",searchDTO);
        log.info("타입 ={}",postType.name());
        log.info("sort = {}", pageable.getSort());


        Query searchQuery = null;

        if (searchDTO.getContent() != null && !searchDTO.getContent().isBlank()) {
            searchQuery = createSearchQuery(searchDTO);
        }

        log.info("searchQuery = {}",searchQuery.toString());

        Query typeFilter = TermQuery.of(t -> t
                .field("postType")
                .value(postType.name())
        )._toQuery();

        if (searchDTO.getContent() != null && !searchDTO.getContent().isBlank()) {
            searchQuery = createSearchQuery(searchDTO);
        }

        BoolQuery.Builder builder = new BoolQuery.Builder()
                .filter(typeFilter);

        if (searchQuery != null) {
            builder.must(searchQuery);
        }

        Query boolQuery = builder.build()._toQuery();

        HighlightQuery highlightQuery = createHighlightQuery(searchDTO.getType());

        NativeQueryBuilder nativeQueryBuilder = NativeQuery.builder()
                .withQuery(boolQuery)
                .withPageable(pageable);

        if (highlightQuery != null) {
            nativeQueryBuilder.withHighlightQuery(highlightQuery);
        }

        NativeQuery nativeQuery = nativeQueryBuilder.build();


        log.info("nativeQuery = {}",nativeQuery.getQuery());

        SearchHits<PostDocument> searchHits = operations.search(nativeQuery, PostDocument.class);

        List<PostDocument> results = searchHits.getSearchHits().stream()
                .map(hit -> {
                    PostDocument content = hit.getContent();

                    List<String> titleHighlights = hit.getHighlightField("title");
                    if (!titleHighlights.isEmpty()) {
                        content.highlightedTitle(titleHighlights.get(0));
                    } else {
                        List<String> ngramHighlights = hit.getHighlightField("title.ngram");
                        if (!ngramHighlights.isEmpty()) {
                            content.highlightedTitle(ngramHighlights.get(0));
                        }
                    }


                    return content;
                })
                .toList();


        List<Long> ids = results.stream().map(d -> Long.valueOf(d.getId())).toList();

        Map<Long, SearchInfo> map = postRepository.search(ids);

        List<PostListDTO> list = new ArrayList<>();

        log.info("result size = {}", results.size());

        for (PostDocument result : results) {

            SearchInfo info = map.get(Long.valueOf(result.getId()));

            list.add(PostListDTO.builder()
                    .id(info.getId())
                    .title(result.getTitle())
                    .nickname(result.getNickname())
                    .viewCount(info.getViewCount())
                    .recommendedNumber(info.getRecommendCount())
                    .createTime(info.getCreateAt())
                    .build());

        }

        return new PageImpl<>(list, pageable, searchHits.getTotalHits());


    }


    private Query createSearchQuery(SearchDTO searchDTO) {
        return switch (searchDTO.getType()) {
            case TITLE -> MultiMatchQuery.of(m -> m
                    .query(searchDTO.getContent())
                    .fields("title", "title.ngram")
                    .type(TextQueryType.Phrase)
            )._toQuery();

            case CONTENT -> MatchQuery.of(m -> m
                    .query(searchDTO.getContent())
                    .field("content")
            )._toQuery();

            case NICKNAME -> MultiMatchQuery.of(m -> m
                    .query(searchDTO.getContent())
                    .fields("nickname", "nickname.ngram")
                    .type(TextQueryType.Phrase)
            )._toQuery();

            case TITLE_CONTENT -> MultiMatchQuery.of(m -> m
                    .query(searchDTO.getContent())
                    .fields("title", "title.ngram", "content")
            )._toQuery();
        };
    }

    private HighlightQuery createHighlightQuery(SearchType searchType) {
        return switch (searchType) {
            case TITLE -> {
                HighlightParameters params = HighlightParameters.builder()
                        .withPreTags("<b>")
                        .withPostTags("</b>")
                        .build();

                Highlight highlight = new Highlight(params,
                        List.of(
                                new HighlightField("title"),
                                new HighlightField("title.ngram")
                        ));
                yield new HighlightQuery(highlight, PostDocument.class);
            }

            case CONTENT -> {
                HighlightParameters params = HighlightParameters.builder()
                        .withPreTags("<mark>")
                        .withPostTags("</mark>")
                        .build();

                Highlight highlight = new Highlight(params,
                        List.of(new HighlightField("content")));
                yield new HighlightQuery(highlight, PostDocument.class);
            }

            case TITLE_CONTENT -> {
                HighlightParameters params = HighlightParameters.builder()
                        .withPreTags("<mark>")
                        .withPostTags("</mark>")
                        .build();

                Highlight highlight = new Highlight(params,
                        List.of(
                                new HighlightField("title"),
                                new HighlightField("title.ngram"),
                                new HighlightField("content")
                        ));
                yield new HighlightQuery(highlight, PostDocument.class);
            }

            case NICKNAME -> null;
        };
    }

//    @PostConstruct
//    public void init() {
//
//        User user = userRepository.save(User.builder()
//                .loginId("tester")
//                .password("encode")
//                .nickname("tester")
//                .email("tester@test.com")
//                .userType(UserType.USER)
//                .build());
//
//        List<Post> posts = new ArrayList<>();
//
//        // 생성할 PostType 배열
//        PostType[] postTypes = {PostType.FREE, PostType.QNA, PostType.INFO, PostType.NOTICE};
//
//        for (PostType type : postTypes) {
//            for (int i = 1; i <= 25_000; i++) {
//                Post post = Post.builder()
//                        .title(type.name() + " 게시글 " + i)
//                        .content("이것은 " + type.name() + " 게시글 내용입니다. 번호: " + i)
//                        .viewCount(0)
//                        .postType(type)
//                        .build();
//
//                post.setWriter(user);
//                posts.add(post);
//
//                if (posts.size() % 10_000 == 0) {
//                    postRepository.saveAll(posts);
//                    posts.clear();
//                }
//            }
//        }
//
//        if (!posts.isEmpty()) {
//            postRepository.saveAll(posts);
//        }
//    }


    private void authCheck(Post post, Long userId, UserType userType) {

        if (!post.getUser().getId().equals(userId) && userType != UserType.ADMIN) {
            throw new RuntimeException("삭제할 권한이 없습니다.");
        }
    }

}
