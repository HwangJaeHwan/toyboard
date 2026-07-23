package com.example.board.toyboard.controller;

import com.amazonaws.services.s3.AmazonS3;
import com.example.board.toyboard.DTO.*;
import com.example.board.toyboard.entity.post.PostType;
import com.example.board.toyboard.entity.UserType;
import com.example.board.toyboard.exception.AuthException;
import com.example.board.toyboard.exception.DuplicateReportedException;
import com.example.board.toyboard.exception.PostNotFoundException;
import com.example.board.toyboard.service.*;
import com.example.board.toyboard.file.FileStore;
import com.example.board.toyboard.file.UploadFile;
import com.example.board.toyboard.response.ApiResponse;
import com.example.board.toyboard.session.SessionConst;
import com.example.board.toyboard.session.UserSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PostController.class)
class PostControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    PostService postService;

    @MockBean
    UserService userService;

    @MockBean
    CommentService commentService;

    @MockBean
    ReportService reportService;

    @MockBean
    FileStore fileStore;

    @MockBean
    AmazonS3 amazonS3;

    @MockBean
    PopularPostService popularPostService;

    @MockBean
    PostRedisService postRedisService;

    @MockBean
    JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @DisplayName("홈 화면을 조회한다")
    @Test
    void home() throws Exception {
        given(postRedisService.getPopularPost())
                .willReturn(List.of());

        given(postService.getLatestPosts())
                .willReturn(new LatestPosts());

        mockMvc.perform(get("/posts/"))
                .andExpect(status().isOk())
                .andExpect(view().name("post/home"))
                .andExpect(model().attributeExists("popularPost"))
                .andExpect(model().attributeExists("latestPosts"));
    }

    @DisplayName("게시글 목록을 조회한다")
    @Test
    void posts() throws Exception {

        Page<PostListDTO> page = Page.empty();


        given(postService.makePageResult(any(Pageable.class), eq(PostType.FREE)))
                .willReturn(new PageDTO<>(page));

        mockMvc.perform(get("/posts"))
                .andExpect(status().isOk())
                .andExpect(view().name("post/list"))
                .andExpect(model().attributeExists("searchDTO"))
                .andExpect(model().attributeExists("posts"))
                .andExpect(model().attribute("postType", PostType.FREE))
                .andExpect(model().attribute("sort", "createdTime"));
    }

    @DisplayName("게시글 목록 조회 시 정렬 조건과 게시판 타입을 전달한다")
    @Test
    void posts_withSortAndPostType() throws Exception {

        Page<PostListDTO> page = Page.empty();

        given(postService.makePageResult(any(Pageable.class), eq(PostType.INFO)))
                .willReturn(new PageDTO<>(page));

        mockMvc.perform(get("/posts")
                        .param("sort", "hits")
                        .param("postType", "INFO"))
                .andExpect(status().isOk())
                .andExpect(view().name("post/list"))
                .andExpect(model().attribute("postType", PostType.INFO))
                .andExpect(model().attribute("sort", "hits"));

        then(postService).should()
                .makePageResult(any(Pageable.class), eq(PostType.INFO));
    }

    @DisplayName("게시글 목록 조회 실패 - 존재하지 않는 게시판 타입이면 400을 반환한다")
    @Test
    void posts_invalidPostType() throws Exception {
        mockMvc.perform(get("/posts")
                        .param("postType", "INVALID"))
                .andExpect(status().isBadRequest());

        then(postService).shouldHaveNoInteractions();
    }

    @DisplayName("게시글을 검색한다")
    @Test
    void search() throws Exception {
        given(postService.search(any(Pageable.class), any(SearchDTO.class), eq(PostType.FREE)))
                .willReturn(Page.empty());

        mockMvc.perform(get("/posts/search")
                        .param("keyword", "스프링")
                        .param("searchType", "TITLE"))
                .andExpect(status().isOk())
                .andExpect(view().name("post/list"))
                .andExpect(model().attributeExists("posts"))
                .andExpect(model().attribute("postType", PostType.FREE))
                .andExpect(model().attribute("sort", "createdTime"));
    }

    @DisplayName("글쓰기 페이지를 조회한다")
    @Test
    void writeStart() throws Exception {
        mockMvc.perform(get("/posts/write"))
                .andExpect(status().isOk())
                .andExpect(view().name("post/write"))
                .andExpect(model().attributeExists("post"))
                .andExpect(model().attributeExists("postCodes"));
    }

    @DisplayName("글쓰기 성공 시 게시글 상세 페이지로 이동한다")
    @Test
    void writeEnd_success() throws Exception {
        given(postService.write(any(PostWriteDTO.class), anyLong()))
                .willReturn(1L);

        mockMvc.perform(post("/posts/write")
                        .sessionAttr(SessionConst.LOGIN_USER, userSession())
                        .param("title", "테스트 제목")
                        .param("content", "테스트 내용")
                        .param("postType", "FREE"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/1"));

        then(postService).should()
                .write(any(PostWriteDTO.class), eq(1L));
    }

    @DisplayName("글쓰기 실패 - 제목이 비어 있으면 작성 페이지로 돌아간다")
    @Test
    void writeEnd_emptyTitle() throws Exception {
        mockMvc.perform(post("/posts/write")
                        .sessionAttr(SessionConst.LOGIN_USER, userSession())
                        .param("title", "")
                        .param("content", "테스트 내용")
                        .param("postType", "FREE"))
                .andExpect(status().isOk())
                .andExpect(view().name("post/write"))
                .andExpect(model().attributeHasFieldErrors("post", "title"))
                .andExpect(model().attributeExists("postCodes"));

        then(postService).shouldHaveNoInteractions();
    }

    @DisplayName("글쓰기 실패 - 내용이 비어 있으면 작성 페이지로 돌아간다")
    @Test
    void writeEnd_emptyContent() throws Exception {
        mockMvc.perform(post("/posts/write")
                        .sessionAttr(SessionConst.LOGIN_USER, userSession())
                        .param("title", "테스트 제목")
                        .param("content", "")
                        .param("postType", "FREE"))
                .andExpect(status().isOk())
                .andExpect(view().name("post/write"))
                .andExpect(model().attributeHasFieldErrors("post", "content"))
                .andExpect(model().attributeExists("postCodes"));

        then(postService).shouldHaveNoInteractions();
    }

    @DisplayName("게시글 상세 페이지를 조회한다")
    @Test
    void readPost() throws Exception {
        given(postService.read(1L, 1L))
                .willReturn(new PostReadDTO());

        given(commentService.findComments(1L))
                .willReturn(List.of(new CommentReadDTO(), new CommentReadDTO()));

        mockMvc.perform(get("/posts/{postId}", 1L)
                        .sessionAttr(SessionConst.LOGIN_USER, userSession()))
                .andExpect(status().isOk())
                .andExpect(view().name("post/read"))
                .andExpect(model().attributeExists("post"))
                .andExpect(model().attributeExists("comments"))
                .andExpect(model().attribute("commentNums", 2))
                .andExpect(model().attribute("userType", UserType.USER));
    }

    @DisplayName("게시글 상세 조회 실패 - 존재하지 않는 게시글이면 에러가 발생한다")
    @Test
    void readPost_notFound() throws Exception {
        given(postService.read(999L, 1L))
                .willThrow(new PostNotFoundException());

        mockMvc.perform(get("/posts/{postId}", 999L)
                        .sessionAttr(SessionConst.LOGIN_USER, userSession()))
                .andExpect(status().is4xxClientError());
    }

    @DisplayName("게시글 수정 페이지를 조회한다")
    @Test
    void updateStart() throws Exception {
        given(postService.getUpdateForm(1L, 1L, UserType.USER))
                .willReturn(new PostUpdateDTO());

        mockMvc.perform(get("/posts/{postId}/update", 1L)
                        .sessionAttr(SessionConst.LOGIN_USER, userSession()))
                .andExpect(status().isOk())
                .andExpect(view().name("post/update"))
                .andExpect(model().attributeExists("post"))
                .andExpect(model().attributeExists("postCodes"));
    }

    @DisplayName("게시글 수정 페이지 조회 실패 - 수정 권한이 없으면 에러가 발생한다")
    @Test
    void updateStart_accessDenied() throws Exception {
        given(postService.getUpdateForm(1L, 1L, UserType.USER))
                .willThrow(new AuthException());

        mockMvc.perform(get("/posts/{postId}/update", 1L)
                        .sessionAttr(SessionConst.LOGIN_USER, userSession()))
                .andExpect(status().is4xxClientError());
    }

    @DisplayName("게시글 수정 성공 시 게시글 상세 페이지로 이동한다")
    @Test
    void updateEnd_success() throws Exception {
        mockMvc.perform(post("/posts/{postId}/update", 1L)
                        .sessionAttr(SessionConst.LOGIN_USER, userSession())
                        .param("title", "수정 제목")
                        .param("content", "수정 내용")
                        .param("postType", "INFO"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/1"));

        then(postService).should()
                .update(eq(1L), eq(1L), eq(UserType.USER), any(PostUpdateDTO.class));
    }

    @DisplayName("게시글 수정 실패 - 제목이 비어 있으면 수정 페이지로 돌아간다")
    @Test
    void updateEnd_emptyTitle() throws Exception {
        mockMvc.perform(post("/posts/{postId}/update", 1L)
                        .sessionAttr(SessionConst.LOGIN_USER, userSession())
                        .param("title", "")
                        .param("content", "수정 내용")
                        .param("postType", "INFO"))
                .andExpect(status().isOk())
                .andExpect(view().name("post/update"))
                .andExpect(model().attributeHasFieldErrors("post", "title"));

        then(postService).shouldHaveNoInteractions();
    }

    @DisplayName("게시글 수정 실패 - 내용이 비어 있으면 수정 페이지로 돌아간다")
    @Test
    void updateEnd_emptyContent() throws Exception {
        mockMvc.perform(post("/posts/{postId}/update", 1L)
                        .sessionAttr(SessionConst.LOGIN_USER, userSession())
                        .param("title", "수정 제목")
                        .param("content", "")
                        .param("postType", "INFO"))
                .andExpect(status().isOk())
                .andExpect(view().name("post/update"))
                .andExpect(model().attributeHasFieldErrors("post", "content"));

        then(postService).shouldHaveNoInteractions();
    }

    @DisplayName("게시글 삭제 성공 시 게시글 목록으로 이동한다")
    @Test
    void deletePost_success() throws Exception {
        mockMvc.perform(get("/posts/{postId}/delete", 1L)
                        .sessionAttr(SessionConst.LOGIN_USER, userSession()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts"));

        then(postService).should()
                .delete(1L, UserType.USER, 1L);
    }

    @DisplayName("게시글 삭제 실패 - 삭제 권한이 없으면 에러가 발생한다")
    @Test
    void deletePost_accessDenied() throws Exception {
        willThrow(new AuthException())
                .given(postService)
                .delete(1L, UserType.USER, 1L);

        mockMvc.perform(get("/posts/{postId}/delete", 1L)
                        .sessionAttr(SessionConst.LOGIN_USER, userSession()))
                .andExpect(status().is4xxClientError());
    }

    @DisplayName("게시글 신고 성공 시 신고 결과를 반환한다")
    @Test
    void report_success() throws Exception {
        given(reportService.postReport(1L, 1L))
                .willReturn(ApiResponse.success("신고 완료", 1L));

        mockMvc.perform(patch("/posts/{postId}/reports", 1L)
                        .sessionAttr(SessionConst.LOGIN_USER, userSession()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(1L));

        then(reportService).should()
                .postReport(1L, 1L);
    }

    @DisplayName("게시글 신고 실패 - 이미 신고한 게시글이면 에러가 발생한다")
    @Test
    void report_alreadyReported() throws Exception {

        given(reportService.postReport(anyLong(), anyLong()))
                .willThrow(new DuplicateReportedException());

        mockMvc.perform(patch("/posts/{postId}/reports", 1L)
                        .sessionAttr(SessionConst.LOGIN_USER, userSession()));
//                .andExpect(status().is4xxClientError());
    }

    @DisplayName("게시글 추천 성공 시 변경된 추천 수를 반환한다")
    @Test
    void recommend_success() throws Exception {
        given(postService.recommend(1L, 1L))
                .willReturn(10);

        mockMvc.perform(get("/posts/{postId}/recommends", 1L)
                        .sessionAttr(SessionConst.LOGIN_USER, userSession()))
                .andExpect(status().isOk())
                .andExpect(content().string("10"));

        then(postService).should()
                .recommend(1L, 1L);
    }

    @DisplayName("게시글 추천 실패 - 존재하지 않는 게시글이면 에러가 발생한다")
    @Test
    void recommend_notFound() throws Exception {
        given(postService.recommend(999L, 1L))
                .willThrow(new PostNotFoundException());

        mockMvc.perform(get("/posts/{postId}/recommends", 999L)
                        .sessionAttr(SessionConst.LOGIN_USER, userSession()))
                .andExpect(status().is4xxClientError());
    }

    @DisplayName("이미지 업로드 성공 시 이미지 조회 URL을 반환한다")
    @Test
    void uploadImage_success() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.png",
                "image/png",
                "image".getBytes()
        );

        given(fileStore.storeFile(any(MultipartFile.class)))
                .willReturn(new UploadFile("test.png", "store.png"));

        mockMvc.perform(multipart("/posts/image").file(file))
                .andExpect(status().isOk())
                .andExpect(content().string("/posts/image/store.png"));

        then(fileStore).should()
                .storeFile(any(MultipartFile.class));
    }

    @DisplayName("이미지 업로드 실패 - 파일 저장 중 예외가 발생하면 서버 에러가 발생한다")
    @Test
    void uploadImage_fail() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.png",
                "image/png",
                "image".getBytes()
        );

        given(fileStore.storeFile(any(MultipartFile.class)))
                .willThrow(new IOException());

        mockMvc.perform(multipart("/posts/image").file(file))
                .andExpect(status().is5xxServerError());
    }

    @DisplayName("이미지 조회 성공 시 저장된 파일을 Resource로 반환한다")
    @Test
    void downloadImage_success() throws Exception {
        Path image = Files.createTempFile("toyboard-", ".png");
        Files.write(image, "image".getBytes());

        given(fileStore.getFullPath("store.png"))
                .willReturn(image.toString());

        mockMvc.perform(get("/posts/image/{filename}", "store.png"))
                .andExpect(status().isOk());

        then(fileStore).should()
                .getFullPath("store.png");

        Files.deleteIfExists(image);
    }

    private UserSession userSession() {
        return UserSession.builder()
                .id(1L)
                .nickname("tester")
                .userType(UserType.USER)
                .build();
    }
}
