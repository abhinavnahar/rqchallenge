package com.reliaquest.api.service;

import com.reliaquest.api.dto.DeleteEmployeeRequest;
import com.reliaquest.api.dto.EmployeeRequest;
import com.reliaquest.api.model.Employee;
import java.util.List;
import java.util.UUID;

public interface IExternalService {
    List<Employee> getAllEmployees();

    List<Employee> getEmployeesByNameSearch(String nameFragment);

    Employee getEmployeeById(UUID id);

    int getHighestSalaryOfEmployees();

    List<String> getTop10HighestEarningEmployeeNames();

    Employee createEmployee(EmployeeRequest employee);

    boolean deleteEmployeeById(DeleteEmployeeRequest deleteEmployeeRequest);
}
