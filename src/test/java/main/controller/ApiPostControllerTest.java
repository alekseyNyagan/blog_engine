package main.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@ActiveProfiles("test-containers-flyway")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApiPostControllerTest {

    @LocalServerPort
    private Integer port;

    private RestTestClient client;

    @BeforeEach
    void setUp() {
        String baseUrl = "http://localhost:" + port;
        client = RestTestClient.bindToServer().baseUrl(baseUrl).build();
    }

    @Test
    @DisplayName("Should return first ten posts, count of all posts and status code 200")
    void shouldGetFirstTenPosts() {
        client.get().uri("/api/post?offset=0&limit=10&mode=recent")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.count").isEqualTo(12)
                .jsonPath("$.posts.length()").isEqualTo(10);
    }

    @Test
    @DisplayName("Should return first posts and count of all posts that contain query, and status code 200")
    void shouldGetFirstPostsThatContainQuery() {
        client.get().uri("/api/post/search?offset=0&limit=10&query=text")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.count").isEqualTo(10)
                .jsonPath("$.posts.length()").isEqualTo(10);
    }

    @Test
    @DisplayName("Should return posts by date, count of posts by date and status code 200")
    void shouldGetPostsByDate() {
        client.get().uri("/api/post/byDate?offset=0&limit=10&date=2024-04-05")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.count").isEqualTo(10)
                .jsonPath("$.posts.length()").isEqualTo(10);
    }

    @Test
    @DisplayName("Should return posts by tag, count of posts by tag and status code 200")
    void shouldGetPostsByTagAndStatus() {
        client.get().uri("/api/post/byTag?offset=0&limit=10&tag=Food")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.count").isEqualTo(1)
                .jsonPath("$.posts.length()").isEqualTo(1);
    }

    @Test
    @DisplayName("Should return post by id, status code 200")
    void shouldGetPostById() {
        client.get().uri("/api/post/1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1);
    }
}
