package main.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import main.error.AbstractError;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorsResponse {
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
