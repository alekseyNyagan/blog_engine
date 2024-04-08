package main.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import main.service.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

@Testcontainers
@ActiveProfiles("test-containers-flyway")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApiPostControllerTest {

    @LocalServerPort
    private Integer port;

    @Autowired
    PostService postService;

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost:" + port;
    }

    @Test
    @DisplayName("Should return first ten posts, count of all posts and status code 200")
    void shouldGetFirstTenPosts() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("api/post?offset=0&limit=10&mode=recent")
                .then()
                .statusCode(200)
                .body("count", equalTo(12))
                .body("posts", hasSize(10));
    }

    @Test
    @DisplayName("Should return first posts and count of all posts that contain query, and status code 200")
    void shouldGetFirstPostsThatContainQuery() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("api/post/search?offset=0&limit=10&mode=search&query=text")
                .then()
                .statusCode(200)
                .body("count", equalTo(10))
                .body("posts", hasSize(10));
    }

    @Test
    @DisplayName("Should return posts by date, count of posts by date and status code 200")
    void shouldGetPostsByDate() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("api/post/byDate?offset=0&limit=10&mode=date&date=2024-04-05")
                .then()
                .statusCode(200)
                .body("count", equalTo(10))
                .body("posts", hasSize(10));
    }

    @Test
    @DisplayName("Should return posts by tag, count of posts by tag and status code 200")
    void shouldGetPostsByTagAndStatus() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("api/post/byTag?offset=0&limit=10&mode=tag&tag=Food")
                .then()
                .statusCode(200)
                .body("count", equalTo(1))
                .body("posts", hasSize(1));
    }

    @Test
    @DisplayName("Should return post by id, status code 200")
    void shouldGetPostById() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("api/post/1")
                .then()
                .statusCode(200);
    }
}
