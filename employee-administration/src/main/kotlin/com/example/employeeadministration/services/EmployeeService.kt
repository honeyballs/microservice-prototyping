package com.example.employeeadministration.services

import com.example.employeeadministration.model.Employee
import com.example.employeeadministration.model.EmployeeDto

interface EmployeeService: MappingService<Employee, EmployeeDto> {

    fun deleteEmployee(id: Long)

}