package com.surgee.stage_2.data;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class UserData {
    private String userId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
}
