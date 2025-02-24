package com.reliaquest.api.service.impl;

import com.reliaquest.api.dto.DeleteEmployeeRequest;
import com.reliaquest.api.dto.EmployeeRequest;
import com.reliaquest.api.exceptions.ApiException;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.service.IEmployeeService;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

@Service
public class EmployeeService implements IEmployeeService {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeService.class);

    private final ExternalService externalService;

    @Autowired
    public EmployeeService(ExternalService externalService) {
        this.externalService = externalService;
    }

    @Override
    public Employee getEmployeeById(UUID id) {
        logger.info("Calling external service to get employee name {}", id);
        return externalService.getEmployeeById(id);
    }

    @Override
    public Employee createEmployee(EmployeeRequest employee) {
        logger.info("Calling external service to create employee {}", employee);
        return externalService.createEmployee(employee);
    }

    @Override
    public List<Employee> getEmployeesByNameSearch(String nameFragment) {
        logger.info("Searching employees by name fragment: {}", nameFragment);
        return getAllEmployees().stream()
                .filter(employee -> employee.employeeName().toLowerCase().contains(nameFragment.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Employee> getAllEmployees() {
        logger.info("Calling external service to get  all employees");
        return externalService.getAllEmployees();
    }

    @Override
    public int getHighestSalaryOfEmployees() {
        logger.info("Figuring out highest salary of the employee");
        return getAllEmployees().stream()
                .mapToInt(Employee::employeeSalary)
                .max()
                .orElseThrow(() -> new ApiException("No employees found", 404));
    }

    @Override
    public List<String> getTop10HighestEarningEmployeeNames() {
        logger.info("Fetching top 10 highest earning employees");
        return getAllEmployees().stream()
                .sorted(Comparator.comparingInt(Employee::employeeSalary).reversed())
                .limit(10)
                .map(Employee::employeeName)
                .collect(Collectors.toList());
    }

    @Override
    public String deleteEmployeeById(String id) {
        logger.info("deleting employee by name:  {}", id);
        Employee employeeById = getEmployeeById(UUID.fromString(id));
        if (employeeById == null) {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "Employee does exist for id: " + id);
        }
        if (externalService.deleteEmployeeById(new DeleteEmployeeRequest(id))) {
            employeeById.employeeName();
        }
        throw new HttpClientErrorException(
                HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete employee with id: " + id);
    }
}
