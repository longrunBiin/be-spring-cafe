package codesquad.codestagram.dto;

import codesquad.codestagram.domain.Article;
import codesquad.codestagram.domain.User;

public class ArticleDto {
    public static class ArticleRequestDto{
        private String title;
        private String content;
        private String userId;

        public ArticleRequestDto(String title, String content, String userId) {
            this.title = title;
            this.content = content;
            this.userId = userId;
        }

        public Article toArticle(User user) {
            return new Article(title, content, user);
        }

        public String getTitle() {
            return title;
        }

        public String getContent() {
            return content;
        }

        public String getUserId() {
            return userId;
        }
    }
}
