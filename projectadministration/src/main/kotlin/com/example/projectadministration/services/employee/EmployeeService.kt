package com.example.projectadministration.services.employee

import com.example.projectadministration.model.aggregates.employee.Employee
import com.example.projectadministration.model.dto.EmployeeDto
import com.example.projectadministration.services.MappingService

interface EmployeeService: MappingService<Employee, EmployeeDto> {

}