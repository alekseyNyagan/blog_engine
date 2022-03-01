package main.controller;

import main.api.request.LoginRequest;
import main.api.request.PasswordRequest;
import main.api.request.RegistrationRequest;
import main.api.request.RestoreRequest;
import main.api.response.*;
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

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

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
    public ErrorsResponse addUser(@RequestBody RegistrationRequest registrationRequest) {
        return usersService.addUser(registrationRequest);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(usersService.login(loginRequest));
    }

    @GetMapping("/logout")
    @PreAuthorize("hasAuthority('user:write')")
    public ResultResponse logout() {
        return usersService.logout();
    }

    @PostMapping("/restore")
    public ResponseEntity<ResultResponse> restore(@RequestBody RestoreRequest restoreRequest, HttpServletRequest httpServletRequest) throws MessagingException {
        return ResponseEntity.ok(usersService.restore(restoreRequest, httpServletRequest));
    }

    @PostMapping("/password")
    public ResponseEntity<ErrorsResponse> password(@RequestBody PasswordRequest passwordRequest) {
        return ResponseEntity.ok(usersService.password(passwordRequest));
    }
}
