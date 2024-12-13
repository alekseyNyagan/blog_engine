package main.controller;

import main.api.response.ErrorsResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@ControllerAdvice
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        BindingResult bindingResult = ex.getBindingResult();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        ErrorsResponse errorsResponse = new ErrorsResponse();
        Map<String, String> errors = new HashMap<>();
        for (FieldError fieldError : fieldErrors) {
            String errorMessage = fieldError.getDefaultMessage();
            String fieldName = fieldError.getField();
            errors.put(fieldName, errorMessage);
        }
        errorsResponse.setErrors(errors);
        return new ResponseEntity<>(errorsResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {NoSuchElementException.class, UsernameNotFoundException.class})
    public ResponseEntity<Object> handleResourceNotFound(RuntimeException ex, WebRequest request) {
        return handleExceptionInternal(ex, ex.getMessage(), new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }
}
