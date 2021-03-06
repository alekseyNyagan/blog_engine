package main.controller;

import main.api.request.PostRequest;
import main.api.request.PostVoteRequest;
import main.api.response.*;
import main.exceptions.NoSuchPostException;
import main.service.GlobalSettingsService;
import main.service.PostServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/post")
public class ApiPostController {

    private final PostServiceImpl postService;
    private final GlobalSettingsService globalSettingsService;

    @Autowired
    public ApiPostController(PostServiceImpl postService, GlobalSettingsService globalSettingsService) {
        this.postService = postService;
        this.globalSettingsService = globalSettingsService;
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
    @PreAuthorize("hasAuthority('user:moderate')")
    public ResponseEntity<PostsResponse> getModerationPosts(@RequestParam int offset, @RequestParam int limit, @RequestParam String status) {
        return new ResponseEntity<>(postService.getModerationPosts(offset, limit, status), HttpStatus.OK);
    }

    @GetMapping("/my")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<PostsResponse> getMyPosts(@RequestParam int offset, @RequestParam int limit, @RequestParam String status) {
        return new ResponseEntity<>(postService.getMyPosts(offset, limit, status), HttpStatus.OK);
    }

    @PostMapping("")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<ErrorsResponse> addPost(@RequestBody PostRequest postRequest) {
        GlobalSettingsResponse globalSettings = globalSettingsService.getGlobalSettings();
        return ResponseEntity.ok(postService.addPost(postRequest, globalSettings.isPostPremoderation()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<ErrorsResponse> updatePost(@PathVariable int id, @RequestBody PostRequest postRequest) {
        GlobalSettingsResponse globalSettings = globalSettingsService.getGlobalSettings();
        return ResponseEntity.ok(postService.updatePost(id, postRequest, globalSettings.isPostPremoderation()));
    }

    @PostMapping("/like")
    @PreAuthorize("hasAuthority('user:write')")
    public ResultResponse like(@RequestBody PostVoteRequest postVoteRequest) {
        return postService.like(postVoteRequest);
    }

    @PostMapping("/dislike")
    @PreAuthorize("hasAuthority('user:write')")
    public ResultResponse dislike(@RequestBody PostVoteRequest postVoteRequest) {
        return postService.dislike(postVoteRequest);
    }
}
