package codesquad.codestagram.service;

import codesquad.codestagram.domain.Article;
import codesquad.codestagram.domain.User;
import codesquad.codestagram.dto.ArticleDto.ArticleRequestDto;
import codesquad.codestagram.repository.ArticleRepository;
import codesquad.codestagram.repository.UserRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ArticleService {
    public static final String NO_USER = "유저가 존재하지 않습니다.";
    public static final String NO_ARTICLE = "게시글이 존재하지 않습니다.";
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;

    public ArticleService(ArticleRepository articleRepository, UserRepository userRepository) {
        this.articleRepository = articleRepository;
        this.userRepository = userRepository;
    }

    public void saveArticle(ArticleRequestDto requestDto) {
        User user = userRepository.findByUserId(requestDto.getUserId())
                .orElseThrow(()->new IllegalArgumentException(NO_USER));

        Article article = requestDto.toArticle(user);
        articleRepository.save(article);
    }

    public List<Article> findArticles() {
        return articleRepository.findAll();
    }

    public Article findArticleById(Long articleId) {
        return articleRepository.findById(articleId)
                .orElseThrow(() -> new IllegalArgumentException(NO_ARTICLE));
    }

    public void updateArticle(Long articleId, String title, String content) {
        Article article = findArticleById(articleId);
        article.update(title, content);
        articleRepository.save(article);
    }

    public void delete(Long articleId) {
        //해당 게시글이 DB에 존재하는지 확인
        Article article = findArticleById(articleId);
        articleRepository.delete(article);
    }
}
