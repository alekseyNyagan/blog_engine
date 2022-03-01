package main.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import main.error.AbstractError;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommentResponse {
    private Integer id;
    private Boolean result;
    private Map<AbstractError, String> errors;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getResult() {
        return result;
    }

    public void setResult(Boolean result) {
        this.result = result;
    }

    public Map<AbstractError, String> getErrors() {
        return errors;
    }

    public void setErrors(Map<AbstractError, String> errors) {
        this.errors = errors;
    }
}
