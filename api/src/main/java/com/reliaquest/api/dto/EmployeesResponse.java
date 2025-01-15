package com.reliaquest.api.dto;

import com.reliaquest.api.model.Employee;
import java.util.List;

public class EmployeesResponse {
    private List<Employee> data;
    private String status;

    public List<Employee> getData() {
        return data;
    }

    public void setData(List<Employee> data) {
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
