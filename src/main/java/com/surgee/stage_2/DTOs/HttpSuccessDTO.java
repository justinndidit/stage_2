package com.surgee.stage_2.DTOs;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class HttpSuccessDTO {
    private String status;
    private String message;
    private Object data;
}
