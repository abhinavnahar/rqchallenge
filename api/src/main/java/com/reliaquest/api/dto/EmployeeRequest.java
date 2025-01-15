package com.reliaquest.api.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;

public record EmployeeRequest(
        @NotBlank String name,
        @Min(1) @NotNull Integer salary,
        @Min(16) @Max(75) @NotNull Integer age,
        @NotBlank String title,
        @Email String email)
        implements Serializable {}
