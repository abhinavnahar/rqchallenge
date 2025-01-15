package com.reliaquest.api.service.impl;

import com.reliaquest.api.dto.*;
import com.reliaquest.api.exceptions.ApiException;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.service.IExternalService;
import io.github.resilience4j.retry.Retry;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class ExternalService implements IExternalService {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeService.class);

    private final RestTemplate restTemplate;
    private final String baseUrl;
    private final Retry retry;

    @Autowired
    public ExternalService(RestTemplate restTemplate, String baseUrl, Retry retry) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
        this.retry = retry;
    }

    @Override
    public Employee getEmployeeById(UUID id) {
        return executeWithRetry(() -> {
            logger.info("Fetching employee by ID: {}", id);
            try {
                ResponseEntity<EmployeeResponse> response =
                        restTemplate.getForEntity(baseUrl + "/" + id, EmployeeResponse.class);
                if (response != null && response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                    return response.getBody().data();
                } else {
                    throw new ApiException("Employee not found", 404);
                }
            } catch (HttpClientErrorException e) {
                throw new ApiException(
                        "Failed to fetch employee by ID: " + id.toString(),
                        e.getStatusCode().value());
            }
        });
    }

    @Override
    public Employee createEmployee(EmployeeRequest employee) {
        return executeWithRetry(() -> {
            logger.info("Creating new employee: {}", employee);
            try {
                EmployeeResponse response = restTemplate.postForObject(baseUrl, employee, EmployeeResponse.class);
                if (response != null && response.data() != null) {
                    return response.data();
                } else {
                    throw new ApiException("Failed to create employee", HttpStatus.INTERNAL_SERVER_ERROR.value());
                }
            } catch (Exception e) {
                throw new ApiException("Failed to create employee", HttpStatus.INTERNAL_SERVER_ERROR.value());
            }
        });
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
        return executeWithRetry(() -> {
            logger.info("Fetching all employees");
            try {
                ResponseEntity<EmployeesResponse> response =
                        restTemplate.getForEntity(baseUrl, EmployeesResponse.class);
                if (response != null && response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                    return response.getBody().getData().stream().collect(Collectors.toList());
                } else {
                    throw new ApiException(
                            "Failed to fetch all employees: " + response.getStatusCode(),
                            response.getStatusCode().value());
                }
            } catch (HttpClientErrorException e) {
                throw new ApiException(
                        "Failed to fetch all employees: " + e.getMessage(),
                        e.getStatusCode().value());
            }
        });
    }

    @Override
    public int getHighestSalaryOfEmployees() {
        logger.info("Fetching the highest salary among employees");
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
    public boolean deleteEmployeeById(DeleteEmployeeRequest deleteEmployeeRequest) {
        HttpEntity<DeleteEmployeeRequest> requestEntity = new HttpEntity<>(deleteEmployeeRequest);
        String id = deleteEmployeeRequest.name();
        return executeWithRetry(() -> {
            logger.info("Deleting employee by ID: {}", id);
            try {
                ResponseEntity<DeleteEmployeeResponse> response =
                        restTemplate.exchange(baseUrl, HttpMethod.DELETE, requestEntity, DeleteEmployeeResponse.class);
                if (response != null && response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                     logger.info("Request to delete employee with ID {} is successfull", id);
                     return response.getBody().success();
                } else {
                    throw new ApiException(
                            "Failed to fetch all employees: " + response.getStatusCode(),
                            response.getStatusCode().value());
                }
            } catch (HttpClientErrorException e) {
                throw new ApiException(
                        "Failed to delete employee: " + e.getMessage(),
                        e.getStatusCode().value());
            }
        });
    }

    private <T> T executeWithRetry(Callable<T> callable) {
        try {
            return Retry.decorateCallable(retry, callable).call();
        } catch (Exception e) {
            if (e instanceof ApiException && ((ApiException) e).getStatusCode() == 429) {
                HttpClientErrorException httpException = (HttpClientErrorException) e;
                throw new ApiException(
                        "Request failed: " + httpException.getMessage(),
                        httpException.getStatusCode().value());
            }
            throw new ApiException("Unexpected error during retry execution: " + e.getMessage(), 500);
        }
    }
}
