package com.surgee.stage_2.DTOs;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class HttpResponse {
    private Object response;
}
