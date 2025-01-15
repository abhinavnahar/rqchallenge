package com.reliaquest.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.UUID;

public record Employee(
        @JsonProperty("id") @NotNull UUID id,
        @JsonProperty("employee_name") @NotBlank String employeeName,
        @JsonProperty("employee_salary") @Min(1) int employeeSalary,
        @JsonProperty("employee_age") @Min(16) @Max(75) int employeeAge,
        @JsonProperty("employee_title") @NotBlank String employeeTitle,
        @JsonProperty("employee_email") @Email String employeeEmail)
        implements Serializable {
    public Employee() {
        this(null, null, 0, 0, null, null);
    }
}
