package com.surgee.stage_2.requests;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

@Data
public class CreateOrganizationRequest {
    @NotBlank(message="Name cannot be left empty")
    private String name;
    private String description;
}
