package com.example.board.toyboard.Config.test;

import com.example.board.toyboard.DTO.PostWriteDTO;
import com.example.board.toyboard.Entity.User;
import com.example.board.toyboard.Entity.UserType;
import com.example.board.toyboard.Repository.UserRepository;
import com.example.board.toyboard.Service.PostService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TestPostInit {

    private final UserRepository userRepository;
    private final PostService postService;
    private final PasswordEncoder encoder;

    @PostConstruct
    public void initTestPost() {

        User user = User.builder()
                .loginId("asd")
                .password(encoder.encode("asd"))
                .nickname("testNickname")
                .email("test@test.com")
                .userType(UserType.USER)
                .build();

        userRepository.save(user);


        initPost(user,"QNA");

        initPost(user,"FREE");

        initPost(user,"NOTICE");

        initPost(user,"INFO");


    }

    private void initPost(User user, String postType) {
        for (int i = 1; i <= 250; i++) {
            PostWriteDTO dto = new PostWriteDTO();
            dto.setTitle(postType + " 테스트 제목 " + i);
            dto.setContent(postType + " 테스트 내용 " + i);
            dto.setPostType(postType); // 예시용 게시물 유형 설정

            postService.write(dto, user);
        }

    }
}
