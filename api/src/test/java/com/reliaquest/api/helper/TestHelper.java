package com.reliaquest.api.helper;

import com.reliaquest.api.model.Employee;
import java.util.List;
import java.util.UUID;

public class TestHelper {
    public static Employee dan() {
        return new Employee(UUID.randomUUID(), "Dan", 3000, 45, "Mr", null);
    }

    public static Employee rajesh() {
        return new Employee(UUID.randomUUID(), "Test", 4000, 35, "Mr", null);
    }

    public static Employee ajay() {
        return new Employee(UUID.randomUUID(), "Test", 5000, 25, "Mr", null);
    }

    public static Employee john() {
        return new Employee(UUID.randomUUID(), "John Dan", 6000, 55, "Mr", null);
    }

    public static List<String> topTenEmployeeNames() {
        List<String> topTenEmployees = List.of(
                "Test Name1",
                "Test Name2",
                "Test Name3",
                "Test Name4",
                "Test Name5",
                "Test Name6",
                "Test Name7",
                "Test Name8",
                "Test Name9",
                "Test Name10");

        return topTenEmployees;
    }
}
