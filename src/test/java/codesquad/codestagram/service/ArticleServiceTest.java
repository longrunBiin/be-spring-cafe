package codesquad.codestagram.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

import codesquad.codestagram.domain.User;
import codesquad.codestagram.dto.ArticleDto;
import codesquad.codestagram.dto.ArticleDto.ArticleRequestDto;
import codesquad.codestagram.repository.ArticleRepository;
import codesquad.codestagram.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class ArticleServiceTest {

    @MockitoBean
    private ArticleRepository articleRepository;
    @MockitoBean
    private UserRepository userRepository;
    @Autowired
    private ArticleService articleService;

    @Test
    @DisplayName("게시글을 작성할 때 회원이 존재 하지 않으면 에러가 발생한다.")
    void writeArticleErrorTest() {
        //given
        given(userRepository.findByUserId("testUser")).willReturn(Optional.empty());
        ArticleRequestDto articleRequestDto = new ArticleRequestDto("testTitle", "content", "testUser");

        //when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> articleService.saveArticle(articleRequestDto));
        //then
        assertThat(exception.getMessage()).isEqualTo("유저가 존재하지 않습니다.");

    }

}