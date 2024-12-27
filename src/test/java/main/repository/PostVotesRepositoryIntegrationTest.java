package main.repository;

import main.model.Post;
import main.model.PostVote;
import main.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataJpaTest
@ActiveProfiles("test-containers-flyway")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PostVotesRepositoryIntegrationTest {

    @Autowired
    private PostVotesRepository postVotesRepository;

    @Autowired
    private PostsRepository postsRepository;

    @Autowired
    private UsersRepository usersRepository;

    private Post post;

    private User user;

    @BeforeEach
    void setUp() {
        user = usersRepository.findById(1).get();
        post = postsRepository.findById(1).get();
    }

    @Test
    @DisplayName("Should find post vote by user and post")
    void shouldFindPostVoteByUserAndPost() {
        Optional<PostVote> postVoteByUserAndPost = postVotesRepository.findPostVoteByUserAndPost(user, post);
        assertThat(postVoteByUserAndPost).isPresent();

    }
}