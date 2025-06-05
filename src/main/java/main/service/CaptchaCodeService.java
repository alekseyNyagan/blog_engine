package main.service;

import main.api.response.CaptchaCodeResponse;

public interface CaptchaCodeService {
    CaptchaCodeResponse getCaptcha();
    boolean isCaptchaNotValid(String secret, String value);
}
