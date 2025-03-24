package codesquad.codestagram.controller;

import static codesquad.codestagram.controller.AuthController.SESSIONED_USER;

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
    public static final String PASSWORD_VALID = "passwordValid";
    public static final String ERROR_MESSAGE = "errorMessage";
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
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


}
