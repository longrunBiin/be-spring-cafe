package codesquad.codestagram.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import codesquad.codestagram.domain.User;
import codesquad.codestagram.dto.UserDto.UserRequestDto;
import codesquad.codestagram.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class UserServiceTest {

    @MockitoBean
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Test
    @DisplayName("회원 가입시 중복된 아이디가 있으면 에러가 발생한다.")
    void registerUserErrorTest() {
        // Given
        User user = new User("testUser", "password123", "test", "test@example.com");
        given(userRepository.findByUserId("testUser")).willReturn(Optional.of(user));
        UserRequestDto requestDto = new UserRequestDto("testUser", "12345", "AAA", "AAA@a.com");

        // When && Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.joinUser(requestDto));

        assertThat(exception.getMessage()).isEqualTo("이미 존재하는 사용자 ID입니다.");
    }
    @Test
    @DisplayName("회원 가입시 중복된 아이디가 없으면 가입에 성공한다.")
    void registerUserTest() {
        // Given
        User user = new User("testUser", "password123", "test", "test@example.com");
        UserRequestDto requestDto = new UserRequestDto("testUser", "password123", "test", "test@example.com");
        given(userRepository.findById(1L)).willReturn(Optional.of(user));

        // When
        userService.joinUser(requestDto);
        User findUser = userRepository.findById(1L).get();

        // Then
        assertThat(findUser.getUserId()).isEqualTo("testUser");
        assertThat(findUser.getPassword()).isEqualTo("password123");
        assertThat(findUser.getName()).isEqualTo("test");
        assertThat(findUser.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("회원 전체 조회시 저장된 모든 회원들을 가져와야 한다.")
    void userListTest(){
        //given
        User user1 = new User("testUser1", "password1", "aaa", "test1@example.com");
        User user2 = new User("testUser2", "password2", "bbb", "test2@example.com");
        User user3 = new User("testUser3", "password3", "ccc", "test3@example.com");
        User user4 = new User("testUser4", "password4", "ddd", "test4@example.com");
        List<User> userList = List.of(user1, user2, user3, user4);
        given(userRepository.findAll()).willReturn(userList);

        //when
        List<User> findUsers = userService.getUserList();

        //then
        assertThat(findUsers.size()).isEqualTo(4);
        assertThat(findUsers).extracting(User::getUserId).containsExactly("testUser1", "testUser2", "testUser3", "testUser4");
        assertThat(findUsers).extracting(User::getPassword).containsExactly("password1", "password2", "password3", "password4");
        assertThat(findUsers).extracting(User::getName).containsExactly("aaa", "bbb", "ccc", "ddd");
        assertThat(findUsers).extracting(User::getEmail).containsExactly("test1@example.com", "test2@example.com", "test3@example.com", "test4@example.com");
    }

    @Test
    @DisplayName("회원 정보 수정시 이름과 이메일이 수정되어야한다.")
    void updateUserTest(){
        //given
        User user = new User("testUser", "password123", "test", "test@example.com");
        given(userRepository.findById(1L)).willReturn(Optional.of(user));

        //when
        userService.updateUser(1L, "changeUser", "change@A.com");

        //then
        assertThat(user.getName()).isEqualTo("changeUser");
        assertThat(user.getEmail()).isEqualTo("change@A.com");
        verify(userRepository).save(user);
    }
}