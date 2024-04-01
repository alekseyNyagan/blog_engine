package main.service;

import main.api.response.CaptchaCodeResponse;

public interface CaptchaCodeService {
    CaptchaCodeResponse getCaptcha();
}
