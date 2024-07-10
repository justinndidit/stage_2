package com.surgee.stage_2.util;

import org.springframework.stereotype.Component;

import com.surgee.stage_2.data.ErrorData;

@Component
public class ErrorDataBuilder {

    public ErrorData buildErrorData(String field, String message) {
        return new ErrorData(field, message);
    }
}

