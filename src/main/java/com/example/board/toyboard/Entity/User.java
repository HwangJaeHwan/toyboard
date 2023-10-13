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
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
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

    @OneToMany(mappedBy = "user")
    @Builder.Default
    private List<Post> posts = new ArrayList<>();


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
