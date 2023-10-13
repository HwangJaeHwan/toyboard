package com.example.board.toyboard.Entity.Report;


import com.example.board.toyboard.Entity.BaseEntity;
import com.example.board.toyboard.Entity.Comment;
import com.example.board.toyboard.Entity.User;
import lombok.*;

import jakarta.persistence.*;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorColumn
public abstract class Report extends BaseEntity {


    public Report(User user) {
        this.user = user;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Long id;


    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;



}
