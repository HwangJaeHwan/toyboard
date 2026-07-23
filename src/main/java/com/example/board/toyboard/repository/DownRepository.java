package com.example.board.toyboard.repository;


import com.example.board.toyboard.entity.Down;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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
