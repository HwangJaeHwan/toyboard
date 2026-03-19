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
import com.example.board.toyboard.Repository.report.PostReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.HighlightQuery;
import org.springframework.data.elasticsearch.core.query.highlight.Highlight;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightField;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final RecommendationRepository recommendationRepository;
    private final PostReportRepository postReportRepository;

    private final PostRedisService redisService;
    private final CommentRepository commentRepository;
    private final LogRepository logRepository;

    private final PostDocumentRepository postDocumentRepository;
    private final ElasticsearchOperations operations;



    @Transactional(readOnly = true)
    public PageDTO<PostListDTO> makePageResult(Pageable pageable, PostType postType) {

        long start = System.currentTimeMillis();

        boolean sortByRecommendation = pageable.getSort().stream()
                .anyMatch(order -> order.getProperty().equals("recommendedNumber"));

        Page<PostListDTO> list = sortByRecommendation
                ? postRepository.findPostsOrderByRecommendation(pageable, postType)
                : postRepository.findPosts(pageable, postType);

        List<Long> ids = list.getContent().stream().map(PostListDTO::getId).toList();
        Map<Long, Long> commentCountMap = postRepository.countCommentsByPostIds(ids);

        if (sortByRecommendation) {
            for (PostListDTO dto : list.getContent()) {
                dto.setCommentNum(commentCountMap.getOrDefault(dto.getId(), 0L));
            }
        } else {
            Map<Long, Long> recommendationCountMap = postRepository.countRecommendationsByPostIds(ids);

            for (PostListDTO dto : list.getContent()) {
                dto.setCommentNum(commentCountMap.getOrDefault(dto.getId(), 0L));
                dto.setRecommendedNumber(recommendationCountMap.getOrDefault(dto.getId(), 0L));
            }
        }

        long end = System.currentTimeMillis();
        log.info("조회 속도 ={} ", end - start + " ms");

        return new PageDTO<>(list);

    }


//    @Transactional(readOnly = true)
//    public Post findPostWithAuthCheck(Long postId, Long userId, UserType userType) {
//
//        Post post = postRepository.findByIdWithUser(postId).orElseThrow(PostNotFoundException::new);
//
//        authCheck(post, userId, userType);
//
//        return post;
//    }

    @Transactional(readOnly = true)
    public PostReadDTO read(Long postId, Long userId) {

        PostReadDTO postReadDTO = postRepository.postRead(postId);

        redisService.incrementViewCount(postId, userId);

        return postReadDTO;
    }
    @Transactional(readOnly = true)
    public LatestPosts getLatestPosts() {

        LatestPosts latestPosts = new LatestPosts();

        latestPosts.getFreeList().addAll(postRepository.findLatestPostsByType(PostType.FREE, 5));
        latestPosts.getInfoList().addAll(postRepository.findLatestPostsByType(PostType.INFO, 5));
        latestPosts.getQnaList().addAll(postRepository.findLatestPostsByType(PostType.QNA, 5));
        latestPosts.getNoticeList().addAll(postRepository.findLatestPostsByType(PostType.NOTICE, 5));

        return latestPosts;

    }

//    @Transactional(readOnly = true)
//    public LatestPosts getLatestPosts() {
//
//        return postRepository.getLatestPosts();
//
//    }

    @Transactional
    public Long write(PostWriteDTO dto, Long userId) {

        User loginUser = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        Post post = Post.create(dto, loginUser);

        Post save = postRepository.save(post);

        logRepository.save(new Log(loginUser, post, LogType.POST));

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

        redisService.saveLatestPost(dto.getPostType(), post.getId());

        return save.getId();

    }

    @Transactional
    public void delete(Long userId, UserType userType, Long postId) {

        Post deletePost = postRepository.findById(postId).orElseThrow(PostNotFoundException::new);

        adminCheck(userId,userType, deletePost);

        logRepository.deleteAllByPost(deletePost);
        commentRepository.deleteAllByPost(deletePost);
        recommendationRepository.deleteAllByPost(deletePost);
        postRepository.delete(deletePost);

        postDocumentRepository.deleteById(deletePost.getId().toString());


    }


    public PostUpdateDTO getUpdateForm(Long postId, Long userId, UserType userType) {
        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);

        adminCheck(userId,userType, post);

        return new PostUpdateDTO(post);
    }

    @Transactional
    public void update(Long postId, Long userId, UserType userType, PostUpdateDTO dto) {

        Post post = postRepository.findById(postId).orElseThrow(PostNotFoundException::new);

        adminCheck(userId,userType, post);

        post.updateTitle(dto.getTitle());
        post.updateContent(dto.getContent());
        post.updatePostType(dto.getPostType());

    }

    @Transactional
    public int recommend(Long postId, Long userId) {


        Post post = postRepository.findById(postId).orElseThrow(PostNotFoundException::new);
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);


        Recommendation recommendation = recommendationRepository
                .findByUserAndPost(user, post)
                .orElse(null);


        if (recommendation != null) {
            recommendationRepository.delete(recommendation);
            logRepository.deleteLogByUserAndPostAndLogType(user, post, LogType.RECOMMEND);
        } else {

            recommendationRepository.save(new Recommendation(user, post));

            logRepository.save(new Log(user, post, LogType.RECOMMEND));
        }


        return recommendationRepository.countAllByPostId(postId);


    }
    @Transactional(readOnly = true)
    public PageDTO<PostReportDTO> postsWithReport(PageListDTO pageListDTO) {

        return new PageDTO<>(postRepository.reportedList(pageListDTO.getPageable()));
    }

    private void adminCheck(Long userId, UserType userType, Post post) {

        if (!post.getUser().getId().equals(userId) && !(userType == UserType.ADMIN)) {
            throw new RuntimeException();
        }
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
                    .hits(info.getHits())
                    .recommendedNumber(info.getRecommendCount())
                    .createdTime(info.getCreateAt())
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
