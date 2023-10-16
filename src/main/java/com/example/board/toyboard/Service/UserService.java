package com.example.board.toyboard.Service;

import com.example.board.toyboard.DTO.*;
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
                .filter(m -> isPassword(loginDTO.getPassword(), m.getPassword()))
                .orElse(null);


    }

    public boolean isPassword(String input , String password) {
        return encoder.matches(input, password);
    }

    @Transactional
    public Long nicknameChange(String nickname, UserEditDTO userEditDTO) {

        User loginUser = userRepository.findByNickname(nickname).orElseThrow(UserNotFoundException::new);

        loginUser.nicknameChange(userEditDTO);

        return loginUser.getId();

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

    @Transactional
    public void passwordChange(String nickname, PasswordChangeDTO dto) {

        log.info("dto = {}", dto.getNewPassword());


        User loginUser = userRepository.findByNickname(nickname).orElseThrow(UserNotFoundException::new);
        log.info("비번1 = {}", loginUser.getPassword());
        String newPassword = encoder.encode(dto.getNewPassword());

        loginUser.passwordChange(newPassword);

        log.info("비번2 = {}", loginUser.getPassword());
    }
}
