package com.example.board.toyboard.repository;

import com.example.board.toyboard.entity.Comment;
import com.example.board.toyboard.entity.Up;
import com.example.board.toyboard.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UpRepository extends JpaRepository<Up, Long> {

    Optional<Up> findByUserAndComment(User user, Comment comment);

    Boolean existsByUserAndComment(User user, Comment comment);



}
