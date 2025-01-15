package com.reliaquest.api.dto;

import com.reliaquest.api.model.Employee;
import java.io.Serializable;

public record EmployeeResponse(Employee data, String status) implements Serializable {
    public EmployeeResponse() {
        this(null, null);
    }
}
