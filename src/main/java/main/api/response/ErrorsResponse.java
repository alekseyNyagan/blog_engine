package main.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import main.error.AbstractError;

import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorsResponse {
    private boolean result;
    private Map<AbstractError, String> errors;
}
