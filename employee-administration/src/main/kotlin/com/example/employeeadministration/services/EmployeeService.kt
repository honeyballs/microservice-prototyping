package com.example.employeeadministration.services

import com.example.employeeadministration.model.aggregates.Employee
import com.example.employeeadministration.model.dto.EmployeeDto

interface EmployeeService: MappingService<Employee, EmployeeDto>, EventProducingPersistenceService<Employee> {

    fun createEmployee(employeeDto: EmployeeDto): EmployeeDto
    fun updateEmployee(employeeDto: EmployeeDto): EmployeeDto
    fun deleteEmployee(id: Long)

}