package com.example.board.toyboard.Entity;


import com.example.board.toyboard.DTO.PostListDTO;
import com.example.board.toyboard.DTO.PostReadDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Post {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    private String title;


    @Column(length = 1000)
    private String content;

    private LocalDateTime createTime;

    private int hits;

    private int recommendedNumber;


    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "post")
    private List<Comment> comments = new ArrayList<>();


    public PostReadDTO makeReadDTO(String nickname) {

        return PostReadDTO.builder()
                .id(id)
                .nickname(nickname)
                .title(title)
                .content(content)
                .createTime(createTime)
                .hits(hits)
                .recommendedNumber(recommendedNumber)
                .build();


    }

    public PostListDTO makeListDTO(String nickname) {

        return PostListDTO.builder()
                .id(id)
                .title(title)
                .nickname(nickname)
                .createTime(createTime)
                .hits(hits)
                .recommendedNumber(recommendedNumber)
                .build();

    }

}
