package com.surgee.stage_2.DTOs;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class HttpFailedDTO {
    private String status;
    private String message;
    private int statusCode;
}
