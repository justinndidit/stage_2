package com.surgee.stage_2.data;

import lombok.Data;
import lombok.Builder;


@Builder
@Data
public class UserCreatedData {
    private String accessToken;
    private UserData user;
}
