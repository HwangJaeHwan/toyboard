package com.example.board.toyboard.Entity;


import com.example.board.toyboard.DTO.PasswordChangeDTO;
import com.example.board.toyboard.DTO.UserEditDTO;
import com.example.board.toyboard.Entity.Post.Post;
import lombok.*;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity{


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(unique = true)
    private String loginId;

    private String password;

    @Column(unique = true)
    private String nickname;

    @Column(unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    private UserType userType;

    @OneToMany(mappedBy = "user")
    private final List<Post> posts = new ArrayList<>();

    @Builder
    public User(String loginId, String password, String nickname, String email, UserType userType) {
        this.loginId = loginId;
        this.password = password;
        this.nickname = nickname;
        this.email = email;
        this.userType = userType;
    }

    public void addPost(Post post) {
        posts.add(post);
    }

    public void nicknameChange(UserEditDTO userEditDTO) {

        this.nickname = userEditDTO.getNickname();

    }

    public void passwordChange(String newPassword) {
        this.password = newPassword;
    }
}
