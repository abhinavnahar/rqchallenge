package com.reliaquest.api.controller;

import static com.reliaquest.api.helper.TestHelper.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reliaquest.api.dto.EmployeeRequest;
import com.reliaquest.api.exceptions.GlobalExceptionHandler;
import com.reliaquest.api.helper.TestHelper;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.service.IEmployeeService;
import java.util.*;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.HttpClientErrorException;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = IEmployeeController.class)
public class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockBean
    private IEmployeeService employeeService;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(IEmployeeController.class)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void testGetAllEmployeesThenReturnAll() throws Exception {
        Employee dhairya = dan();
        Employee rajesh = rajesh();
        Employee ajay = ajay();
        Employee john = john();

        List<Employee> allEmployees = List.of(dhairya, rajesh, ajay, john);

        given(employeeService.getAllEmployees()).willReturn(allEmployees);

        mockMvc.perform(get("/api/v1/employee").contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[0].employee_name", is(dhairya.employeeName())))
                .andExpect(jsonPath("$[0].employee_salary", is(dhairya.employeeSalary())))
                .andExpect(jsonPath("$[0].employee_age", is(dhairya.employeeAge())));
        ;
    }

    @Test
    void testGetAllEmployeesThrowsTooManyRequestException() throws Exception {
        given(employeeService.getAllEmployees()).willThrow(new HttpClientErrorException(HttpStatus.TOO_MANY_REQUESTS));

        mockMvc.perform(get("/api/v1/employee").contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(429));
    }

    @Test
    void testGetEmployeeById() throws Exception {
        Employee dhairya = dan();

        given(employeeService.getEmployeeById(dhairya.id())).willReturn(dhairya);

        mockMvc.perform(get("/api/v1/employee/{id}", dhairya.id()).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employee_name", is(dhairya.employeeName())))
                .andExpect(jsonPath("$.employee_salary", is(dhairya.employeeSalary())))
                .andExpect(jsonPath("$.employee_age", is(dhairya.employeeAge())));
        ;
    }

    @Test
    void testGetEmployeeByIdThrowsTooManyRequestException() throws Exception {
        given(employeeService.getEmployeeById(any()))
                .willThrow(new HttpClientErrorException(HttpStatus.TOO_MANY_REQUESTS));

        mockMvc.perform(get("/api/v1/employee/{id}", UUID.randomUUID()).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(429));
    }

    @Test
    void testGetEmployeeByIdThrowsInternalServerError() throws Exception {
        given(employeeService.getEmployeeById(any()))
                .willThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

        mockMvc.perform(get("/api/v1/employee/{id}", UUID.randomUUID()).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(500));
    }

    @Test
    void testGetEmployeeByIdThrowsEmployeeNotFoundException() throws Exception {
        given(employeeService.getEmployeeById(any())).willThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        mockMvc.perform(get("/api/v1/employee/{id}", UUID.randomUUID()).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(404));
    }

    @Test
    void testGetEmployeeByNameSearch() throws Exception {

        String searchString = "da";

        given(employeeService.getEmployeesByNameSearch(searchString)).willReturn(List.of(dan(), john()));

        Employee dan = dan();
        mockMvc.perform(get("/api/v1/employee/search/{searchString}", searchString)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].employee_name", is(dan.employeeName())))
                .andExpect(jsonPath("$[0].employee_salary", is(dan.employeeSalary())))
                .andExpect(jsonPath("$[0].employee_age", is(dan.employeeAge())));
    }

    @Test
    void testGetEmployeeByNameSearchEmptyEmployeeList() throws Exception {

        String searchString = "dh";
        given(employeeService.getEmployeesByNameSearch(searchString)).willReturn(new ArrayList<>());

        mockMvc.perform(get("/api/v1/employee/search/{searchString}", searchString)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void testGetHighestSalaryOfEmployee() throws Exception {
        given(employeeService.getHighestSalaryOfEmployees()).willReturn(john().employeeSalary());

        mockMvc.perform(get("/api/v1/employee/highest-salary"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("6000"));
    }

    @Test
    void testGetTopTenHighestEarningEmployeeNames() throws Exception {

        given(employeeService.getTop10HighestEarningEmployeeNames()).willReturn(TestHelper.topTenEmployeeNames());

        mockMvc.perform(get("/api/v1/employee/top-ten-salaries").contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(10)))
                .andExpect(jsonPath("$.[0]", is("Test Name1")))
                .andExpect(jsonPath("$.[9]", is("Test Name10")));
    }

    @Test
    void testCreateEmployee() throws Exception {
        given(employeeService.createEmployee(any())).willReturn(john());

        mockMvc.perform(post("/api/v1/employee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new EmployeeRequest("test", 32434, 34, "Mr", null))))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.employee_name", is(john().employeeName())))
                .andExpect(jsonPath("$.employee_salary", is(john().employeeSalary())));
    }

    @Test
    void testDeleteEmployee() throws Exception {
        given(employeeService.deleteEmployeeById(any())).willReturn("Test Name");

        mockMvc.perform(delete("/api/v1/employee/{id}", UUID.randomUUID()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Test Name"));
    }
}
