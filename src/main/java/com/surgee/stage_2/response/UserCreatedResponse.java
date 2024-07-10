package com.surgee.stage_2.response;

import com.surgee.stage_2.data.UserCreatedData;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserCreatedResponse {
    private UserCreatedData data;
}
