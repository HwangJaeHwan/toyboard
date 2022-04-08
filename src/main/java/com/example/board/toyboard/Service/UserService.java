package com.example.board.toyboard.Service;

import com.example.board.toyboard.DTO.LoginDTO;
import com.example.board.toyboard.DTO.RegisterDTO;
import com.example.board.toyboard.Entity.User;
import com.example.board.toyboard.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;


    public User findById(Long userId) {

        return userRepository.findById(userId).orElse(null);


    }

    @Transactional
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

    public Boolean loginIdDuplicationCheck(String loginId) {

        return userRepository.findByLoginId(loginId).isPresent();
    }

    public Boolean nicknameDuplicationCheck(String nickname) {

        return userRepository.findByNickname(nickname).isPresent();
    }

    public Boolean emailDuplicationCheck(String email) {

        return userRepository.findByEmail(email).isPresent();
    }

    public User login(LoginDTO loginDTO) {

        return userRepository.findByLoginId(loginDTO.getLoginId())
                .filter(m -> encoder.matches(loginDTO.getPassword(), m.getPassword()))
                .orElse(null);

    }




}
