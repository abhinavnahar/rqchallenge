package com.reliaquest.api.controller.impl;

import static org.springframework.http.ResponseEntity.*;

import com.reliaquest.api.controller.IEmployeeController;
import com.reliaquest.api.dto.EmployeeRequest;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.service.IEmployeeService;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@Validated
@RequestMapping("/api/v1/employee")
public class EmployeeController implements IEmployeeController<Employee, EmployeeRequest> {
    private IEmployeeService employeeService;

    @Autowired
    public void EmployeeControllerImpl(IEmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public ResponseEntity<List<Employee>> getAllEmployees() {
        log.info("Handling request to fetch all employees");
        return ok(employeeService.getAllEmployees());
    }

    @GetMapping("/search/{nameFragment}")
    public ResponseEntity<List<Employee>> getEmployeesByNameSearch(@PathVariable String nameFragment) {
        log.info("Handling request to search employees with name fragment: {}", nameFragment);
        return ok(employeeService.getEmployeesByNameSearch(nameFragment));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable String id) {
        log.info("Handling request to fetch employee by ID: {}", id);
        Employee employee = employeeService.getEmployeeById(UUID.fromString(id));
        if (employee == null) {
            return status(HttpStatus.NOT_FOUND).body(null);
        }
        return ok(employee);
    }

    @GetMapping("/highest-salary")
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        log.info("Handling request to fetch the highest salary of employees");
        return ok(employeeService.getHighestSalaryOfEmployees());
    }

    @GetMapping("/top-ten-salaries")
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        log.info("Handling request to fetch top 10 highest earning employees");
        return ok(employeeService.getTop10HighestEarningEmployeeNames());
    }

    @PostMapping
    public ResponseEntity<Employee> createEmployee(@RequestBody @Validated EmployeeRequest employee) {
        log.info("Handling request to create a new employee");
        Employee createdEmployee = employeeService.createEmployee(employee);
        return status(HttpStatus.CREATED).body(createdEmployee);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEmployeeById(@PathVariable String id) {
        log.info("Handling request to delete employee by ID: {}", id);
        return ok(employeeService.deleteEmployeeById(UUID.fromString(id)));
    }
}
