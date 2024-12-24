package main.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import main.api.request.PostRequest;
import main.api.request.PostVoteRequest;
import main.api.response.*;
import main.dto.CurrentPostDto;
import main.service.PostServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Post controller", description = "Controller for operations that related to posts")
@RestController
@RequestMapping("/api/post")
public class ApiPostController {

    private final PostServiceImpl postService;

    @Autowired
    public ApiPostController(PostServiceImpl postService) {
        this.postService = postService;
    }

    @Operation(summary = "Get posts", description = "Get page of posts with chosen mode")
    @GetMapping("")
    public ResponseEntity<PostsResponse> getPosts(@RequestParam @Parameter(description = "Offset for pagination") int offset
            , @RequestParam @Parameter(description = "Limit of posts for pagination") int limit
            , @RequestParam @Parameter(description = "Sort mode") String mode) {
        return new ResponseEntity<>(postService.getPosts(offset, limit, mode), HttpStatus.OK);
    }

    @Operation(summary = "Get posts", description = "Get page of posts that contain given query")
    @GetMapping("/search")
    public ResponseEntity<PostsResponse> search(@RequestParam @Parameter(description = "Offset for pagination") int offset
            , @RequestParam @Parameter(description = "Limit of posts for pagination") int limit
            , @RequestParam @Parameter(description = "Query for search") String query) {
        if (query.isBlank()) {
            return new ResponseEntity<>(postService.getPosts(offset, limit, "recent"), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(postService.getPostsByQuery(offset, limit, query), HttpStatus.OK);
        }
    }

    @Operation(summary = "Get posts by date", description = "Get page of posts that contain given date")
    @GetMapping("/byDate")
    public ResponseEntity<PostsResponse> getPostsByDate(@RequestParam @Parameter(description = "Offset for pagination") int offset
            , @RequestParam @Parameter(description = "Limit of posts for pagination") int limit
            , @RequestParam @Parameter(description = "Date") String date) {
        return new ResponseEntity<>(postService.getPostsByDate(offset, limit, date), HttpStatus.OK);
    }

    @Operation(summary = "Get posts by tag", description = "Get page of posts that contain given tag")
    @GetMapping("/byTag")
    public ResponseEntity<PostsResponse> getPostsByTag(@RequestParam @Parameter(description = "Offset for pagination") int offset
            , @RequestParam @Parameter(description = "Limit of posts for pagination") int limit
            , @RequestParam @Parameter(description = "Tag") String tag) {
        return new ResponseEntity<>(postService.getPostsByTag(offset, limit, tag), HttpStatus.OK);
    }

    @Operation(summary = "Get post by id")
    @GetMapping("/{id}")
    public ResponseEntity<CurrentPostDto> getPostById(@PathVariable @Parameter(description = "Post id") int id) {
        return ResponseEntity.status(HttpStatus.OK).body(postService.getPostById(id));
    }

    @Operation(summary = "Get posts that need moderation")
    @GetMapping("/moderation")
    @PreAuthorize("hasAuthority('user:moderate')")
    public ResponseEntity<PostsResponse> getModerationPosts(@RequestParam @Parameter(description = "Offset for pagination") int offset
            , @RequestParam @Parameter(description = "Limit of posts for pagination") int limit
            , @RequestParam @Parameter(description = "Moderation status of posts") String status) {
        return new ResponseEntity<>(postService.getModerationPosts(offset, limit, status), HttpStatus.OK);
    }

    @Operation(summary = "Get page of my posts")
    @GetMapping("/my")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<PostsResponse> getMyPosts(@RequestParam @Parameter(description = "Offset for pagination") int offset
            , @RequestParam @Parameter(description = "Limit of posts for pagination") int limit
            , @RequestParam @Parameter(description = "Moderation status of posts") String status) {
        return new ResponseEntity<>(postService.getMyPosts(offset, limit, status), HttpStatus.OK);
    }

    @Operation(summary = "Add post", description = "Add new post")
    @PostMapping("")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<ResultResponse> addPost(@RequestBody @Parameter(description = """
            Request body for posting new post
            """) @Valid PostRequest postRequest) {
        return ResponseEntity.ok(postService.addPost(postRequest));
    }

    @Operation(summary = "Update post")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<ResultResponse> updatePost(@PathVariable @Parameter(description = "Post id to update") int id
            , @RequestBody @Parameter(description = """
            Request body for updating post
            """) PostRequest postRequest) {
        return ResponseEntity.ok(postService.updatePost(id, postRequest));
    }

    @Operation(summary = "Make like")
    @PostMapping("/like")
    @PreAuthorize("hasAuthority('user:write')")
    public ResultResponse like(@RequestBody @Parameter(description = "Request body for making post like") PostVoteRequest postVoteRequest) {
        return postService.like(postVoteRequest);
    }

    @Operation(summary = "Make dislike")
    @PostMapping("/dislike")
    @PreAuthorize("hasAuthority('user:write')")
    public ResultResponse dislike(@RequestBody @Parameter(description = "Request body for making post dislike") PostVoteRequest postVoteRequest) {
        return postService.dislike(postVoteRequest);
    }
}
