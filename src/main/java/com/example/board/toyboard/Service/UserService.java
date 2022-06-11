package com.example.board.toyboard.Service;

import com.example.board.toyboard.DTO.LogDTO;
import com.example.board.toyboard.DTO.LoginDTO;
import com.example.board.toyboard.DTO.PageResultDTO;
import com.example.board.toyboard.DTO.RegisterDTO;
import com.example.board.toyboard.Entity.User;
import com.example.board.toyboard.Entity.log.Log;
import com.example.board.toyboard.Entity.log.LogType;
import com.example.board.toyboard.Exception.UserNotFoundException;
import com.example.board.toyboard.Repository.LogRepository;
import com.example.board.toyboard.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Function;


@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final LogRepository logRepository;
    private final PasswordEncoder encoder;


    public User findById(Long userId) {

        return userRepository.findById(userId).orElseThrow(UserNotFoundException::new);


    }

    public User findByNickname(String nickname) {
        return userRepository.findByNickname(nickname).orElseThrow(UserNotFoundException::new);
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

        log.info("유저 정보 = {}", savedUser);

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

    public PageResultDTO<LogDTO, Log> findLogsByUser(User user, Pageable pageable) {

        Page<Log> logs = logRepository.findLogsByUser(user, pageable);

        Function<Log, LogDTO> fn = (log -> new LogDTO(log));

        return new PageResultDTO<>(logs, fn);

    }

    public PageResultDTO<LogDTO, Log> findPostLogsByUser(User user, Pageable pageable) {

        Page<Log> logs = logRepository.findPostLogsByUser(user, LogType.POST, pageable);

        Function<Log, LogDTO> fn = (log -> new LogDTO(log));

        return new PageResultDTO<>(logs, fn);

    }




}
