package com.surgee.stage_2.DTOs;

import java.util.ArrayList;

import lombok.Data;
import lombok.Builder;

import com.surgee.stage_2.data.ErrorData;

@Data
@Builder
public class ErrorDTO {
    private ArrayList<ErrorData> errors;
}
