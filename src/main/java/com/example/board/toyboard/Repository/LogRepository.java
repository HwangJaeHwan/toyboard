package com.example.board.toyboard.Repository;

import com.example.board.toyboard.Entity.log.Log;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogRepository extends JpaRepository<Log, Long> {
}
