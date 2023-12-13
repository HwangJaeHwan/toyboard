package com.example.board.toyboard.Service;

import com.example.board.toyboard.DTO.*;
import com.example.board.toyboard.Entity.User;
import com.example.board.toyboard.Entity.UserType;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final LogRepository logRepository;
    private final PasswordEncoder encoder;

    private static final String ID_PATTERN = "^(?=.*[a-zA-Z0-9]).{5,20}$";
    private static final String PASSWORD_PATTERN = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,20}";

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
                .userType(UserType.USER)
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

    public Boolean idPatternCheck(String loginId) {
        Pattern pattern = Pattern.compile(ID_PATTERN);
        Matcher matcher = pattern.matcher(loginId);
        return matcher.matches();
    }

    public Boolean passwordPatternCheck(String password) {
        Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    public User login(LoginDTO loginDTO) {

        return userRepository.findByLoginId(loginDTO.getLoginId())
                .filter(m -> isPassword(loginDTO.getPassword(), m.getPassword()))
                .orElse(null);


    }

    public boolean isPassword(String input, String password) {
        return encoder.matches(input, password);
    }

    @Transactional
    public Long nicknameChange(String nickname, UserEditDTO userEditDTO) {

        User loginUser = userRepository.findByNickname(nickname).orElseThrow(UserNotFoundException::new);

        loginUser.nicknameChange(userEditDTO);

        return loginUser.getId();

    }

    public PageConvertDTO<LogDTO, Log> findLogsByUser(User user, Pageable pageable) {

        Page<Log> logs = logRepository.findLogsByUser(user, pageable);

        Function<Log, LogDTO> fn = (LogDTO::new);

        return new PageConvertDTO<>(logs, fn);

    }

    public PageConvertDTO<LogDTO, Log> findPostLogsByUser(User user, Pageable pageable) {

        Page<Log> logs = logRepository.findPostLogsByUser(user, LogType.POST, pageable);

        Function<Log, LogDTO> fn = (LogDTO::new);

        return new PageConvertDTO<>(logs, fn);

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

    public PageConvertDTO<UserListDTO, User> makePageResult(Pageable pageable, SearchDTO searchDTO) {


        Page<User> entity = userRepository.search(pageable, searchDTO);
        Function<User, UserListDTO> fn = UserListDTO::new;
        return new PageConvertDTO<>(entity, fn);

    }
}
