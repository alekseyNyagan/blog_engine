package main.controller;

import main.api.response.PostsResponse;
import main.service.PostServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/post")
public class ApiPostController {

    private final PostServiceImpl postService;

    @Autowired
    public ApiPostController(PostServiceImpl postService) {
        this.postService = postService;
    }

    @GetMapping("")
    private ResponseEntity<PostsResponse> getPosts(@RequestParam int offset, @RequestParam int limit, @RequestParam String mode) {
        if (postService.postCount() == 0) {
            return new ResponseEntity<>(new PostsResponse(), HttpStatus.OK);
        }
        PostsResponse postsResponse = new PostsResponse();
        postsResponse.setPosts(postService.getPosts(offset, limit, mode));
        postsResponse.setCount(postService.postCount());
        return new ResponseEntity<>(postsResponse, HttpStatus.OK);
    }
}
