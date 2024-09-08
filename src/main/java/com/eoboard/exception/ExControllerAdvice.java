package com.eoboard.exception;

import com.eoboard.security.handler.ErrorResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(annotations = RestController.class)
public class ExControllerAdvice {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(IllegalArgumentException.class)
    public ErrorResponse illigalExHandler(IllegalArgumentException e) {
        return new ErrorResponse(HttpServletResponse.SC_NOT_FOUND, "존재하지 않는 요청입니다.");
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NullPointerException.class)
    public ErrorResponse nullPointerExHandler(NullPointerException e) {
        return new ErrorResponse(HttpServletResponse.SC_NOT_FOUND, "존재하지 않는 요청입니다.");
    }
}
