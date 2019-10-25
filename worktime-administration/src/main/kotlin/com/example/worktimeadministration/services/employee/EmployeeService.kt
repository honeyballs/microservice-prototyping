package com.example.worktimeadministration.services.employee

import com.example.worktimeadministration.model.aggregates.employee.Employee
import com.example.worktimeadministration.model.dto.employee.EmployeeDto
import com.example.worktimeadministration.services.MappingService

interface EmployeeService: MappingService<Employee, EmployeeDto> {
}