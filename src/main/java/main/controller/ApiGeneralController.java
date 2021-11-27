package main.controller;

import main.api.response.GlobalSettingsResponse;
import main.api.response.InitResponse;
import main.api.response.TagsResponse;
import main.service.GlobalSettingsService;
import main.service.TagsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ApiGeneralController {

    private final InitResponse initResponse;
    private final GlobalSettingsService globalSettingsService;
    private final TagsService tagsService;

    @Autowired
    public ApiGeneralController(InitResponse initResponse, GlobalSettingsService globalSettingsService, TagsService tagsService) {
        this.initResponse = initResponse;
        this.globalSettingsService = globalSettingsService;
        this.tagsService = tagsService;
    }

    @GetMapping("/tag")
    private TagsResponse getTags() {
        return tagsService.getTags();
    }

    @GetMapping("/settings")
    private GlobalSettingsResponse getGlobalSettings() {
        return globalSettingsService.getGlobalSettings();
    }

    @GetMapping("/init")
    private InitResponse init() {
        return initResponse;
    }
}
