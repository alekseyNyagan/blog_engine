package main.controller;

import main.api.response.CalendarResponse;
import main.api.response.GlobalSettingsResponse;
import main.api.response.InitResponse;
import main.api.response.TagsResponse;
import main.service.GlobalSettingsService;
import main.service.PostService;
import main.service.TagsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ApiGeneralController {

    private final InitResponse initResponse;
    private final GlobalSettingsService globalSettingsService;
    private final TagsService tagsService;
    private final PostService postService;

    @Autowired
    public ApiGeneralController(InitResponse initResponse, GlobalSettingsService globalSettingsService, TagsService tagsService
    , PostService postService) {
        this.initResponse = initResponse;
        this.globalSettingsService = globalSettingsService;
        this.tagsService = tagsService;
        this.postService = postService;
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
}
