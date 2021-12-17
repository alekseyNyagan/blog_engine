package main.controller;

import main.api.request.LoginRequest;
import main.api.request.RegistrationRequest;
import main.api.response.*;
import main.error.AbstractError;
import main.service.CaptchaCodeService;
import main.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
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
    public ResponseEntity<LoginResponse> check(Principal principal) {
        if (principal == null) {
            return ResponseEntity.ok(new LoginResponse());
        }
        return ResponseEntity.ok(usersService.check(principal.getName()));
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

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(usersService.login(loginRequest));
    }

    @GetMapping("/logout")
    @PreAuthorize("hasAuthority('user:write')")
    public LogoutResponse logout() {
        return usersService.logout();
    }
}
