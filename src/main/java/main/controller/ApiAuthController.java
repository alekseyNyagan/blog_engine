package main.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
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

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import java.security.Principal;

@Tag(name = "Authentication controller", description = "Controller for operations that relate to authentication")
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

    @Operation(summary = "Check if user is logged in", description = """
    Checks if user is logged in and information about authenticated user
    """)
    @GetMapping("/check")
    public ResponseEntity<LoginResponse> check(Principal principal) {
        if (principal == null) {
            return ResponseEntity.ok(new LoginResponse());
        }
        return ResponseEntity.ok(usersService.check(principal.getName()));
    }

    @Operation(summary = "Get picture of captcha from the server")
    @GetMapping("/captcha")
    public CaptchaCodeResponse getCaptcha() {
        return captchaCodeService.getCaptcha();
    }

    @Operation(summary = "Register new user")
    @PostMapping("/register")
    public ErrorsResponse addUser(@RequestBody @Parameter(description = """
            Information about new user
            """) RegistrationRequest registrationRequest) {
        return usersService.addUser(registrationRequest);
    }

    @Operation(summary = "Login user")
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Parameter(description = """
            Information with user credentials to login
            """) LoginRequest loginRequest) {
        return ResponseEntity.ok(usersService.login(loginRequest));
    }

    @Operation(summary = "Logout user")
    @GetMapping("/logout")
    @PreAuthorize("hasAuthority('user:write')")
    public ResultResponse logout() {
        return usersService.logout();
    }

    @Operation(summary = "Restore password", description = "Send information message to registered email to restore password")
    @PostMapping("/restore")
    public ResponseEntity<ResultResponse> restore(@RequestBody @Parameter(description = """
        Request body with registered email to restore password
        """) RestoreRequest restoreRequest, HttpServletRequest httpServletRequest) throws MessagingException {
        return ResponseEntity.ok(usersService.restore(restoreRequest, httpServletRequest));
    }

    @Operation(summary = "Change password")
    @PostMapping("/password")
    public ResponseEntity<ErrorsResponse> password(@RequestBody @Parameter(description = """
        Request body with information to change password
        """) PasswordRequest passwordRequest) {
        return ResponseEntity.ok(usersService.password(passwordRequest));
    }
}
