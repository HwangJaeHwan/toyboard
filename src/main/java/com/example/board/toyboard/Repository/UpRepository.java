package com.example.board.toyboard.Repository;

import com.example.board.toyboard.Entity.Comment;
import com.example.board.toyboard.Entity.Up;
import com.example.board.toyboard.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UpRepository extends JpaRepository<Up, Long> {

    public Optional<Up> findByUserAndComment(User user, Comment comment);


}
