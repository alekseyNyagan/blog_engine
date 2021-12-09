package main.api.response;

import main.error.AbstractError;

import java.util.Map;

public class RegistrationResponse {
    private boolean result;
    private Map<AbstractError, String> errors;

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public Map<AbstractError, String> getErrors() {
        return errors;
    }

    public void setErrors(Map<AbstractError, String> errors) {
        this.errors = errors;
    }
}
