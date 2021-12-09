package main.controller;

import main.api.request.RegistrationRequest;
import main.api.response.CaptchaCodeResponse;
import main.api.response.CheckResponse;
import main.api.response.RegistrationResponse;
import main.error.AbstractError;
import main.service.CaptchaCodeService;
import main.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("api/auth")
public class ApiAuthController {

    private final CaptchaCodeService captchaCodeService;
    private final UserService usersService;

    @Autowired
    public ApiAuthController(CaptchaCodeService captchaCodeService, UserService usersService) {
        this.captchaCodeService = captchaCodeService;
        this.usersService = usersService;
    }

    @GetMapping("/check")
    public CheckResponse check() {
        CheckResponse checkResponse = new CheckResponse();
        checkResponse.setResult(false);
        return checkResponse;
    }

    @GetMapping("/captcha")
    public CaptchaCodeResponse getCaptcha() {
        return captchaCodeService.getCaptcha();
    }

    @PostMapping("/register")
    public RegistrationResponse addUser(@RequestBody RegistrationRequest registrationRequest) {
        RegistrationResponse registrationResponse = new RegistrationResponse();
        Map<AbstractError, String> errors = usersService.addUser(registrationRequest);
        if (!errors.isEmpty()) {
            registrationResponse.setErrors(errors);
        } else {
            registrationResponse.setResult(true);
        }
        return registrationResponse;
    }
}
