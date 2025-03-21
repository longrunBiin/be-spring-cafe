package codesquad.codestagram.service;

import static codesquad.codestagram.service.ArticleService.NO_USER;

import codesquad.codestagram.domain.User;
import codesquad.codestagram.dto.UserDto.UserRequestDto;
import codesquad.codestagram.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    public static final String USER_ALREADY_EXIST = "이미 존재하는 사용자 ID입니다.";
    public static final String FAIL_SING_IN = "아이디와 비밀번호가 일치하지 않습니다.";
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostConstruct
    public void initDB() {
        userRepository.save(new User("javajigi", "test", "자바지기", "javajigi@slipp.net"));
        userRepository.save(new User("sanjigi", "test", "산지기", "sanjigi@slipp.net"));
    }

    public void joinUser(UserRequestDto requestDto) {
        //같은 아이디를 가지는 유저가 있는지 확인
        if(userRepository.findByUserId(requestDto.getUserId()).isPresent())
                throw new IllegalArgumentException(USER_ALREADY_EXIST);

        // 중복되지 않으면 사용자 저장
        User user = requestDto.toUser();
        userRepository.save(user);
    }

    public List<User> getUserList() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(NO_USER));
    }

    public User updateUser(Long id, String name, String email) {
        User user = getUserById(id);
        user.updateUser(name, email);
        userRepository.save(user);

        return user;
    }

    public User getUserForLogin(String userId, String password) {
       return userRepository.findByUserIdAndPassword(userId, password).orElseThrow(() -> new IllegalArgumentException(FAIL_SING_IN));
    }
}
