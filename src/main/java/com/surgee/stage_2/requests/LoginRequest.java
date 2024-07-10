package com.surgee.stage_2.requests;

import lombok.Data;
import lombok.Builder;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;


@Builder
@Data
public class LoginRequest {
    @NotBlank(message="Email field cannot be empty")
    @Email(message="Please enter a valid Email")
    private String email;

    @NotBlank(message="Password field cannot be empty")
    private String password;

}
