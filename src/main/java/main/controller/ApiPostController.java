package main.controller;

import main.api.response.PostsResponse;
import main.exceptions.NoSuchPostException;
import main.service.PostServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    public ResponseEntity<PostsResponse> getPosts(@RequestParam int offset, @RequestParam int limit, @RequestParam String mode) {
        return new ResponseEntity<>(postService.getPosts(offset, limit, mode), HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<PostsResponse> search(@RequestParam int offset, @RequestParam int limit, String query) {
        if (query.isBlank()) {
            return new ResponseEntity<>(postService.getPosts(offset, limit, "recent"), HttpStatus.OK) ;
        } else {
            return new ResponseEntity<>(postService.getPostsByQuery(offset, limit, query), HttpStatus.OK);
        }
    }

    @GetMapping("/byDate")
    public ResponseEntity<PostsResponse> getPostsByDate(@RequestParam int offset, @RequestParam int limit, @RequestParam String date) {
        return new ResponseEntity<>(postService.getPostsByDate(offset, limit, date), HttpStatus.OK);
    }

    @GetMapping("/byTag")
    public ResponseEntity<PostsResponse> getPostsByTag(@RequestParam int offset, @RequestParam int limit, @RequestParam String tag) {
        return new ResponseEntity<>(postService.getPostsByTag(offset, limit, tag), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity getPostById(@PathVariable int id) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(postService.getPostById(id));
        } catch (NoSuchPostException exception){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
        }
    }

    @GetMapping("/moderation")
    public ResponseEntity<PostsResponse> getModerationPosts(@RequestParam int offset, @RequestParam int limit, @RequestParam String status) {
        return new ResponseEntity<>(postService.getModerationPosts(offset, limit, status), HttpStatus.OK);
    }
}
