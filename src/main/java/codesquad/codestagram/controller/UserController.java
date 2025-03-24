package codesquad.codestagram.controller;

import codesquad.codestagram.domain.User;
import codesquad.codestagram.dto.UserDto;
import codesquad.codestagram.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserController {
    public static final String USER = "user";
    public static final String SESSIONED_USER = "sessionedUser";
    public static final String PASSWORD_VALID = "passwordValid";
    public static final String ERROR_MESSAGE = "errorMessage";
    public static final String JSESSION_ID = "JSESSIONID";
    public static final String ROOT_DIRECTORY = "/";
    public static final int ONE_HOUR = 3600;
    public static final int ZERO  = 0;
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/users")
    public String signUp(@ModelAttribute UserDto.UserRequestDto requestDto, Model model) {
        try{
            userService.joinUser(requestDto);
        }catch (IllegalArgumentException e){
            // 이미 존재하는 user_id일 경우
            model.addAttribute(ERROR_MESSAGE, e.getMessage());
            return "register";  // 회원가입 페이지로 다시 돌아감
        }
        return "redirect:/users";
    }

    @GetMapping("/users")
    public String showUsers(Model model) {
        List<User> users = userService.getUserList();
        model.addAttribute(USER, users);
        return "user/list";
    }

    @GetMapping("/users/{id}")
    public String showUserProfile(@PathVariable Long id, Model model) {
        try{
            User user = userService.getUserById(id);
            model.addAttribute(USER, user);
        }catch (IllegalArgumentException e){
            model.addAttribute(ERROR_MESSAGE, e.getMessage());
        }
        return "user/profile";
    }

    @GetMapping("/users/{id}/update")
    public String editUser(Model model, @PathVariable Long id, HttpSession httpSession, RedirectAttributes redirectAttributes) {
        if (ArticleController.checkLogin(httpSession)) return "redirect:/login";

        try{
            User user = (User) httpSession.getAttribute(SESSIONED_USER);
            user.matchId(id);
            model.addAttribute(USER, user);
        }catch (IllegalArgumentException e){
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, e.getMessage());
            return "redirect:/users";
        }
        return "user/updateForm";  // 정보 수정 페이지로 이동
    }

    @GetMapping("/users/verify_password")
    public String verifyPassword(@RequestParam String password, @RequestParam Long id, Model model) {
        User user = userService.getUserById(id);
        model.addAttribute(USER, user);

        if (user.matchPassword(password)) {
            model.addAttribute(PASSWORD_VALID, true);
        }else {
            model.addAttribute(PASSWORD_VALID, false);
        }

        return "user/updateForm"; // 실패 시 프로필 화면으로 돌아가기
    }

    @PutMapping("users/update")
    public String updateProfile(@RequestParam String name, @RequestParam String email, @RequestParam Long id, Model model) {
        User user = userService.updateUser(id, name, email);

        model.addAttribute(USER, user);
        return "redirect:/users";
    }

    @PostMapping("users/sign-in")
    public String signInUser(@RequestParam String userId, @RequestParam String password,
                             HttpSession httpSession, Model model) {
        try {
            //아이디와 비밀번호가 일치하는 User가 없으면 에러 출력
            User user = userService.getUserForLogin(userId, password);
            //세션에 user데이터 등록
            httpSession.setAttribute(SESSIONED_USER, user);
            // 세션 설정
            httpSession.setMaxInactiveInterval(ONE_HOUR); // 1시간
        }catch (IllegalArgumentException e){
            model.addAttribute(ERROR_MESSAGE, e.getMessage());
            return "user/signIn";
        }

        return "redirect:/";  // 로그인 성공시 홈페이지로 리다이렉트
    }

    @GetMapping("users/sign-out")
    public String logout(HttpSession httpSession, HttpServletResponse response){
        httpSession.invalidate();

        // 클라이언트 쿠키에서 세션 ID 삭제
        Cookie cookie = new Cookie(JSESSION_ID, null); // 세션 쿠키 삭제
        cookie.setMaxAge(ZERO);  // 즉시 만료
        cookie.setPath(ROOT_DIRECTORY);   // 모든 경로에서 적용
        response.addCookie(cookie);

        return "redirect:/";
    }
}
