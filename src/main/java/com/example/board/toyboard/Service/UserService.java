package com.example.board.toyboard.Service;

import com.example.board.toyboard.DTO.LoginDTO;
import com.example.board.toyboard.DTO.RegisterDTO;
import com.example.board.toyboard.Entity.User;
import com.example.board.toyboard.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;


    public Long save(RegisterDTO registerDTO) {

        String encode = encoder.encode(registerDTO.getPassword());


        User user = User.builder()
                .loginId(registerDTO.getLoginId())
                .password(encode)
                .nickname(registerDTO.getNickname())
                .email(registerDTO.getEmail())
                .build();

        User savedUser = userRepository.save(user);

        return savedUser.getId();


    }


    public User login(LoginDTO loginDTO) {

        return userRepository.findByLoginId(loginDTO.getLoginId())
                .filter(m -> encoder.matches(loginDTO.getPassword(), m.getPassword()))
                .orElse(null);



    }




}
