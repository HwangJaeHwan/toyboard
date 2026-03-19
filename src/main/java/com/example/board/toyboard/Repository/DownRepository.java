package com.example.board.toyboard.Repository;


import com.example.board.toyboard.Entity.Comment;
import com.example.board.toyboard.Entity.Down;
import com.example.board.toyboard.Entity.Up;
import com.example.board.toyboard.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DownRepository extends JpaRepository<Down, Long> {

//    Optional<Down> findByUserAndComment(User user, Comment comment);
//
//<<<<<<< Updated upstream
//    Boolean existsByUserAndComment(User user, Comment comment);
//=======
//    boolean existsByUserAndComment(User user, Comment comment);
//>>>>>>> Stashed changes

}
