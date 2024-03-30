package com.andriidnikitin.tools.mockcdn.advice;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(value = {RuntimeException.class})
  protected ResponseEntity<Object> handleUnexpectedError(RuntimeException ex, WebRequest request) {
    return handleExceptionInternal(
        ex, "Unexpected error occurred", new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
  }
}
