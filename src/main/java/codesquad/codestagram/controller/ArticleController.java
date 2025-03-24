package codesquad.codestagram.controller;

import static codesquad.codestagram.controller.AuthController.SESSIONED_USER;
import static codesquad.codestagram.controller.UserController.ERROR_MESSAGE;

import codesquad.codestagram.domain.Article;
import codesquad.codestagram.domain.User;
import codesquad.codestagram.dto.ArticleDto;
import codesquad.codestagram.service.ArticleService;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ArticleController {
    public static final String ARTICLES = "articles";

    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @PostMapping("/articles")
    public String writeArticle(@ModelAttribute ArticleDto.ArticleRequestDto requestDto, Model model) {
        try {
            articleService.saveArticle(requestDto);
        }catch (IllegalArgumentException e){
            model.addAttribute(ERROR_MESSAGE, e.getMessage());
            return "articles/form";
        }
        return "redirect:/";
    }

    @GetMapping("/")
    public String showArticles(Model model) {
        List<Article> articles = articleService.findArticles();
        model.addAttribute(ARTICLES, articles);
        return "home";
    }

    @GetMapping("articles/{articleId}")
    public String showArticleDetail(@PathVariable Long articleId, Model model, HttpSession session) {
        if (checkLogin(session)) return "redirect:/login";

        try {
            Article article = articleService.findArticleById(articleId);

            User sessionedUser = (User) session.getAttribute(SESSIONED_USER);
            if (sessionedUser.getUserId().equals(article.getUser().getUserId())) {
                model.addAttribute("author", true);
            }

            model.addAttribute(article);
        }catch (IllegalArgumentException e){
            model.addAttribute(ERROR_MESSAGE, e.getMessage());
            return "redirect:/";
        }
        return "articles/show";
    }

    @GetMapping("articles/{articleId}/edit")
    public String editArticleForm(@PathVariable Long articleId, Model model, HttpSession session) {

        if (checkLogin(session)) return "redirect:/login";

        try {
            Article article = articleService.findArticleById(articleId);
            model.addAttribute(article);
        }catch (IllegalArgumentException e){
            model.addAttribute(ERROR_MESSAGE, e.getMessage());
            return "redirect:/articles/" + articleId;
        }
        return "articles/edit";
    }

    @PutMapping("articles/{articleId}")
    public String updateArticle(@PathVariable Long articleId, @RequestParam String title,
                              @RequestParam String content, HttpSession session, Model model) {
        if (checkLogin(session)) return "redirect:/login";

        try {
            articleService.updateArticle(articleId, title, content);
        }catch (IllegalArgumentException e){
            model.addAttribute(ERROR_MESSAGE, e.getMessage());
            return "redirect:/articles/" + articleId;
        }
        return "redirect:/articles/" + articleId;
    }

    @DeleteMapping("articles/{articleId}")
    public String deleteArticle(@PathVariable Long articleId, HttpSession session, Model model) {
        if (checkLogin(session)) return "redirect:/login";

        try {
            articleService.delete(articleId);
        }catch (IllegalArgumentException e){
            model.addAttribute(ERROR_MESSAGE, e.getMessage());
            return "redirect:/";
        }
        return "redirect:/";
    }

    public static boolean checkLogin(HttpSession session) {
        User sessionedUser = (User) session.getAttribute(SESSIONED_USER);
        return sessionedUser == null;
    }
}
