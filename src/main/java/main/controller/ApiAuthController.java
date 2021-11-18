package main.controller;

import main.api.response.CheckResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/auth")
public class ApiAuthController {

    @GetMapping("/check")
    public CheckResponse check() {
        CheckResponse checkResponse = new CheckResponse();
        checkResponse.setResult(false);
        return checkResponse;
    }
}
