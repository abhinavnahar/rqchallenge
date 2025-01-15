package com.reliaquest.api.dto;

import java.io.Serializable;

public record DeleteEmployeeResponse(boolean success, String status) implements Serializable {}
