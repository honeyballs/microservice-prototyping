package com.example.employeeadministration.services

import com.example.employeeadministration.model.aggregates.Employee
import com.example.employeeadministration.model.dto.EmployeeDto

interface EmployeeService: MappingService<Employee, EmployeeDto>, EventProducingPersistenceService<Employee> {

    fun getAllEmployees(): List<EmployeeDto>
    fun getEmployeeById(id: Long): EmployeeDto
    fun getEmployeesOfDepartment(departmentId: Long): List<EmployeeDto>
    fun getEmployeesByPosition(positionId: Long): List<EmployeeDto>
    fun getEmployeesByName(firstname: String, lastname: String): List<EmployeeDto>

    fun createEmployee(employeeDto: EmployeeDto): EmployeeDto
    fun updateEmployee(employeeDto: EmployeeDto): EmployeeDto
    fun deleteEmployee(id: Long)

}