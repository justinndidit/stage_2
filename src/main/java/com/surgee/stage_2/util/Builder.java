package com.surgee.stage_2.util;

import com.surgee.stage_2.DTOs.ErrorDTO;
import com.surgee.stage_2.data.ErrorData;

import java.util.ArrayList;

import org.springframework.stereotype.Component;

@Component
public class Builder {
    public ErrorData buildErrorData(String field, String message) {
        return ErrorData.builder()
                        .field("Authorization Token")
                        .message("Unauthorized: Missing or malformed Bearer token!")                            
                        .build();
    }

    public ArrayList<ErrorData> convertToErrorDataList(ErrorData errorData) {
        ArrayList<ErrorData> errorDataList = new ArrayList<>();
        errorDataList.add(errorData);
        return errorDataList;
    }

    public ErrorDTO buildErrorDTO(ArrayList<ErrorData> errorDataList) {
        return ErrorDTO.builder()
                       .errors(errorDataList)
                       .build();
    }
}
