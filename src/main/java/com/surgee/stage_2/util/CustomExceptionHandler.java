package com.surgee.stage_2.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.surgee.stage_2.data.ErrorData;

import lombok.RequiredArgsConstructor;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
@RequiredArgsConstructor
public class CustomExceptionHandler {

    private final ErrorDataBuilder builder;

    // public CustomExceptionHandler(ErrorDataBuilder builder) {
    //     this.builder = builder;
    // }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            WebRequest request) {

        Map<String, Object> body = new LinkedHashMap<>();

        List<ErrorData> errors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(error -> builder.buildErrorData(error.getField(), error.getDefaultMessage()))
                .collect(Collectors.toList());

        body.put("errors", errors);

        return new ResponseEntity<>(body, HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
