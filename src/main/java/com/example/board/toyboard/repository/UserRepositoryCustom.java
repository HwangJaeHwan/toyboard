package com.example.board.toyboard.repository;

import com.example.board.toyboard.DTO.UserSearch;
import com.example.board.toyboard.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserRepositoryCustom {

    Page<User> search(Pageable pageable, UserSearch search);


}
