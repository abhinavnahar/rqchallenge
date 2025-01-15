package com.reliaquest.api.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.reliaquest.api.dto.EmployeeRequest;
import com.reliaquest.api.exceptions.ApiException;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.service.impl.EmployeeService;
import com.reliaquest.api.service.impl.ExternalService;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class EmployeeServiceTest {

    @InjectMocks
    private EmployeeService mockService;

    @Mock
    private ExternalService externalService;

    private Employee mockEmployee;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockEmployee = new Employee(UUID.randomUUID(), "John Doe", 50000, 30, "Developer", "johndoe@example.com");
    }

    @Test
    public void testGetAllEmployeesService() {
        when(externalService.getAllEmployees()).thenReturn(Arrays.asList(mockEmployee));
        List<Employee> employees = mockService.getAllEmployees();

        assertNotNull(employees);
        assertEquals(1, employees.size());
        assertEquals("John Doe", employees.get(0).employeeName());
    }

    @Test
    public void getTop10HighestEarningEmployeeNames() {
        Employee employee =
                new Employee(UUID.randomUUID(), "Highest", 100000, 30, "Highest Developer", "Highest@example.com");
        when(externalService.getAllEmployees()).thenReturn(Arrays.asList(mockEmployee, employee));
        List<String> employees = mockService.getTop10HighestEarningEmployeeNames();

        assertNotNull(employees);
        assertEquals(2, employees.size());
        assertEquals("Highest", employees.get(0));
    }

    @Test
    public void getHighestSalaryOfEmployees() {
        Employee employee =
                new Employee(UUID.randomUUID(), "Highest", 100000, 30, "Highest Developer", "Highest@example.com");
        when(externalService.getAllEmployees()).thenReturn(Arrays.asList(mockEmployee, employee));
        int salary = mockService.getHighestSalaryOfEmployees();

        assertEquals(100000, salary);
    }

    @Test
    public void getEmployeesByNameSearch() {
        Employee employee =
                new Employee(UUID.randomUUID(), "Highest", 100000, 30, "Highest Developer", "Highest@example.com");
        when(externalService.getAllEmployees()).thenReturn(Arrays.asList(mockEmployee, employee));
        List<Employee> employees = mockService.getEmployeesByNameSearch("IgHes");

        assertNotNull(employees);
        assertEquals(1, employees.size());
        assertEquals("Highest", employees.get(0).employeeName());
    }

    @Test
    public void testGetEmployeeByIdService() {
        UUID id = UUID.randomUUID();
        when(externalService.getEmployeeById(id)).thenReturn(mockEmployee);
        Employee employee = mockService.getEmployeeById(id);

        assertNotNull(employee);
        assertEquals("John Doe", employee.employeeName());
    }

    @Test
    public void testCreateEmployeeService() {
        EmployeeRequest employeeRequest =
                new EmployeeRequest("John Doe", 50000, 30, "Developer", "johndoe@example.com");
        when(externalService.createEmployee(employeeRequest)).thenReturn(mockEmployee);
        Employee createdEmployee = mockService.createEmployee(employeeRequest);

        assertNotNull(createdEmployee);
        assertEquals("John Doe", createdEmployee.employeeName());
    }

    @Test
    public void testHandleApiException() {
        ApiException apiException = new ApiException("Employee not found", 404);

        assertEquals(404, apiException.getStatusCode());
        assertEquals("Employee not found", apiException.getMessage());
    }

    @Test
    public void testHandleEmptyEmployeeList() {
        when(externalService.getAllEmployees()).thenThrow(new ApiException("No employees found", 404));

        ApiException exception = assertThrows(ApiException.class, () -> mockService.getAllEmployees());

        assertEquals(404, exception.getStatusCode());
        assertEquals("No employees found", exception.getMessage());
    }
}
