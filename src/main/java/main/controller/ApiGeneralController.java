package main.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import main.api.request.CommentRequest;
import main.api.request.ModerationRequest;
import main.api.request.SettingsRequest;
import main.api.request.UpdateProfileRequest;
import main.api.response.*;
import main.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Tag(name = "General controller", description = "Controller for operations that do not relate to post and authorization operations")
@RestController
@RequestMapping("/api")
public class ApiGeneralController {

    private final InitResponse initResponse;
    private final GlobalSettingsService globalSettingsService;
    private final TagsService tagsService;
    private final PostService postService;
    private final PostCommentService postCommentService;
    private final UserService userService;

    @Autowired
    public ApiGeneralController(InitResponse initResponse, GlobalSettingsService globalSettingsService, TagsService tagsService
            , PostService postService, PostCommentService postCommentService, UserService userService) {
        this.initResponse = initResponse;
        this.globalSettingsService = globalSettingsService;
        this.tagsService = tagsService;
        this.postService = postService;
        this.postCommentService = postCommentService;
        this.userService = userService;
    }

    @Operation(summary = "Get calendar with posts count"
            , description = "Get calendar with posts count by each day for current year")
    @GetMapping("/calendar")
    public CalendarResponse getCalendar(@RequestParam @Parameter(description = "Year") int year) {
        return postService.getCalendar(year);
    }

    @Operation(summary = "Get tags", description = "Get tags with their weights")
    @GetMapping("/tag")
    public TagsResponse getTags() {
        return tagsService.getTags();
    }

    @Operation(summary = "Get global settings", description = "Get settings for entire blog")
    @GetMapping("/settings")
    public GlobalSettingsResponse getGlobalSettings() {
        return globalSettingsService.getGlobalSettings();
    }

    @Operation(summary = "Get information about blog")
    @GetMapping("/init")
    public InitResponse init() {
        return initResponse;
    }

    @Operation(summary = "Add comment", description = "Add comment to post or reply to another comment")
    @PostMapping("/comment")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<CommentResponse> addComment(@RequestBody @Parameter(description = """
            Comment text and post id that user want to add to and id of comment to reply to
            """) @Valid CommentRequest commentRequest) {
        return ResponseEntity.status(HttpStatus.OK).body(postCommentService.addComment(commentRequest));
    }

    @Operation(summary = "Get current user statistics")
    @GetMapping("/statistics/my")
    @PreAuthorize("hasAuthority('user:write')")
    public StatisticsResponse getMyStatistic() {
        return postService.getMyStatistic();
    }

    @Operation(summary = "Get entire blog statistics")
    @GetMapping("/statistics/all")
    public ResponseEntity<StatisticsResponse> getAllStatistic() {
        if (!globalSettingsService.getGlobalSettings().isMultiuserMode() && userService.getUser().getIsModerator() != 1) {
            return ResponseEntity.status(401).build();
        } else {
            return ResponseEntity.ok(postService.getAllStatistic());
        }
    }

    @Operation(summary = "Update user profile")
    @PostMapping(value = "/profile/my", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('user:write')")
    public ErrorsResponse updateProfile(@RequestBody @Parameter(description = """
            Request body with info for updating profile of user
            """) UpdateProfileRequest updateProfileRequest) {
        return userService.updateUser(updateProfileRequest);
    }

    @Operation(summary = "Update user profile with photo")
    @PostMapping(value = "/profile/my", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('user:write')")
    public ErrorsResponse updateProfileWithPhoto(@ModelAttribute @Parameter(description = """
            Request body with info for updating profile of user
            """) UpdateProfileRequest updateProfileRequest) throws IOException {
        return userService.updateUserWithPhoto(updateProfileRequest);
    }

    @Operation(summary = "Moderate post", description = "Records the action of the moderator: accept or decline the post")
    @PostMapping("/moderation")
    @PreAuthorize("hasAuthority('user:moderate')")
    public ResponseEntity<ResultResponse> moderation(@RequestBody @Parameter(description = """
            Request body with info for moderation
            """) ModerationRequest moderationRequest) {
        return ResponseEntity.ok(postService.moderation(moderationRequest));
    }

    @Operation(summary = "Update blog settings")
    @PutMapping("/settings")
    @PreAuthorize("hasAuthority('user:moderate')")
    public void updateGlobalSettings(@RequestBody @Parameter(description = """
            Request body with settings should be updated
            """) SettingsRequest settingsRequest) {
        globalSettingsService.updateGlobalSettings(settingsRequest);
    }

    @Operation(summary = "Upload image on server")
    @PostMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<Object> image(@RequestParam("image") @Parameter(description = "Image for uploading") MultipartFile multipartFile) throws IOException {
        Object response = postService.image(multipartFile);
        if (response instanceof String) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
}
