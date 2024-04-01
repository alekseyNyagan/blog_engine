package main.controller;

import main.api.request.*;
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

    @GetMapping("/calendar")
    public CalendarResponse getCalendar(@RequestParam int year) {
        return postService.getCalendar(year);
    }

    @GetMapping("/tag")
    public TagsResponse getTags() {
        return tagsService.getTags();
    }

    @GetMapping("/settings")
    public GlobalSettingsResponse getGlobalSettings() {
        return globalSettingsService.getGlobalSettings();
    }

    @GetMapping("/init")
    public InitResponse init() {
        return initResponse;
    }

    @PostMapping("/comment")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity addComment(@RequestBody CommentRequest commentRequest) {
        CommentResponse commentResponse = postCommentService.addComment(commentRequest);
        if (commentResponse.getErrors() == null) {
            return ResponseEntity.status(HttpStatus.OK).body(commentResponse);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(commentResponse);
        }
    }

    @GetMapping("/statistics/my")
    @PreAuthorize("hasAuthority('user:write')")
    public StatisticsResponse getMyStatistic() {
        return postService.getMyStatistic();
    }

    @GetMapping("/statistics/all")
    public ResponseEntity<StatisticsResponse> getAllStatistic() {
        if (!globalSettingsService.getGlobalSettings().isMultiuserMode() && userService.getUser().getIsModerator() != 1) {
            return ResponseEntity.status(401).build();
        } else {
            return ResponseEntity.ok(postService.getAllStatistic());
        }
    }

    @PostMapping(value = "/profile/my", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('user:write')")
    public ErrorsResponse updateProfile(@RequestBody UpdateProfileRequest updateProfileRequest) {
        return userService.updateUser(updateProfileRequest);
    }

    @PostMapping(value = "/profile/my", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('user:write')")
    public ErrorsResponse updateProfileWithPhoto(@ModelAttribute UpdateProfileRequest updateProfileRequest) throws IOException {
        return userService.updateUserWithPhoto(updateProfileRequest);
    }

    @PostMapping("/moderation")
    @PreAuthorize("hasAuthority('user:moderate')")
    public ResponseEntity<ResultResponse> moderation(@RequestBody ModerationRequest moderationRequest) {
        return ResponseEntity.ok(postService.moderation(moderationRequest));
    }

    @PutMapping("/settings")
    @PreAuthorize("hasAuthority('user:moderate')")
    public void updateGlobalSettings(@RequestBody SettingsRequest settingsRequest) {
        globalSettingsService.updateGlobalSettings(settingsRequest);
    }

    @PostMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity image(@RequestParam("image") MultipartFile multipartFile) throws IOException {
        Object response = postService.image(multipartFile);
        if (response instanceof String) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
}
