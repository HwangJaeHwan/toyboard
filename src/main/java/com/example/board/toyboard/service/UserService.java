package com.example.board.toyboard.service;

import com.example.board.toyboard.DTO.*;
import com.example.board.toyboard.entity.User;
import com.example.board.toyboard.entity.UserType;
import com.example.board.toyboard.entity.log.Log;
import com.example.board.toyboard.entity.log.LogType;
import com.example.board.toyboard.exception.*;
import com.example.board.toyboard.repository.LogRepository;
import com.example.board.toyboard.repository.UserRepository;
import com.example.board.toyboard.repository.post.PostRepository;
import com.example.board.toyboard.session.UserSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final LogRepository logRepository;
    private final PostRepository postRepository;
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
    public Long save(RegisterDTO dto) {


        if (!dto.getPassword().equals(dto.getPasswordCheck())) {
            throw new PasswordMismatchException();
        }
        if (userRepository.existsByLoginId(dto.getLoginId())) {
            throw new DuplicateLoginIdException();
        }
        if (userRepository.existsByNickname(dto.getNickname())) {
            throw new DuplicateNicknameException();
        }
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateEmailException();
        }


        String encode = encoder.encode(dto.getPassword());


        User user = User.builder()
                .loginId(dto.getLoginId())
                .password(encode)
                .nickname(dto.getNickname())
                .email(dto.getEmail())
                .userType(UserType.USER)
                .build();

        User savedUser = userRepository.save(user);

        log.info("유저 정보 = {}", savedUser);

        return savedUser.getId();


    }


//    public Boolean loginIdDuplicationCheck(String loginId) {
//
//        return userRepository.findByLoginId(loginId).isPresent();
//    }
//
//    public Boolean nicknameDuplicationCheck(String nickname) {
//
//        return userRepository.findByNickname(nickname).isPresent();
//    }
//
//    public Boolean emailDuplicationCheck(String email) {
//
//        return userRepository.findByEmail(email).isPresent();
//    }

//    public Boolean idPatternCheck(String loginId) {
//        Pattern pattern = Pattern.compile(ID_PATTERN);
//        Matcher matcher = pattern.matcher(loginId);
//        return matcher.matches();
//    }
//
//    public Boolean passwordPatternCheck(String password) {
//        Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
//        Matcher matcher = pattern.matcher(password);
//        return matcher.matches();
//    }

    public UserSession login(LoginDTO dto) {

        User user = userRepository.findByLoginId(dto.getLoginId())
                .orElseThrow(LoginFailException::new);

        if (!encoder.matches(dto.getPassword(), user.getPassword())) {
            throw new LoginFailException();
        }

        return UserSession.builder()
                .id(user.getId())
                .userType(user.getUserType())
                .nickname(user.getNickname())
                .build();
    }

    private boolean isPassword(String input, String password) {
        return encoder.matches(input, password);
    }

    @Transactional
    public UserSession nicknameChange(UserSession userSession, UserEditDTO dto) {

        User user = userRepository.findById(userSession.getId())
                .orElseThrow(UserNotFoundException::new);

        boolean exists = userRepository.existsByNickname(dto.getNickname());
        if (exists && !user.getNickname().equals(dto.getNickname())) {
            throw new DuplicateNicknameException();
        }

        user.nicknameChange(dto.getNickname());

        return UserSession.builder()
                .id(user.getId())
                .userType(userSession.getUserType())
                .nickname(user.getNickname())
                .build();
    }


    public MyPageDTO findLogsByUser(Long userId, Pageable pageable) {

        User user = getUser(userId);

        Page<Log> logs = logRepository.findLogsByUser(user, pageable);

        return toMyPageDTO(user, logs);


    }

    private User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
    }

    public MyPageDTO findPostLogsByUser(Long userId, Pageable pageable) {

        User user = getUser(userId);

        Page<Log> logs = logRepository.findPostLogsByUser(user, LogType.POST, pageable);

        return toMyPageDTO(user, logs);


    }


    private MyPageDTO toMyPageDTO(User user, Page<Log> logs) {

        long postCount = postRepository.countByUserId(user.getId());

        return MyPageDTO.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .postNum(postCount)
                .logs(new PageConvertDTO<>(logs, LogDTO::new))
                .build();
    }



    @Transactional
    public void passwordChange(Long userId, PasswordChangeDTO dto) {

        User loginUser = getUser(userId);

        validatePasswordChange(loginUser, dto);

        String encodedPassword = encoder.encode(dto.getNewPassword());
        loginUser.passwordChange(encodedPassword);
    }

    private void validatePasswordChange(User loginUser, PasswordChangeDTO dto) {
        if (!encoder.matches(dto.getNowPassword(), loginUser.getPassword())) {
            throw new InvalidPasswordException();
        }

        if (!dto.getNewPassword().equals(dto.getNewPasswordCheck())) {
            throw new PasswordMismatchException();
        }

        if (dto.getNowPassword().equals(dto.getNewPassword())) {
            throw new SamePasswordException();
        }
    }


    public PageConvertDTO<UserListDTO, User> makePageResult(Pageable pageable, UserSearch search) {

        Page<User> entity = userRepository.search(pageable, search);
        return new PageConvertDTO<>(entity, UserListDTO::new);

    }
}
