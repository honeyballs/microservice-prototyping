package com.example.employeeadministration.controllers

import com.example.employeeadministration.model.dto.EmployeeDto
import com.example.employeeadministration.model.valueobjects.Address
import com.example.employeeadministration.model.valueobjects.BankDetails
import org.springframework.http.ResponseEntity
import java.math.BigDecimal

interface EmployeeController {

    fun getAllEmployees(): ResponseEntity<List<EmployeeDto>>
    fun getEmployeeById(id: Long): ResponseEntity<EmployeeDto>
    fun getEmployeesOfDepartment(departmentId: Long): ResponseEntity<List<EmployeeDto>>
    fun getEmployeesByPosition(positionId: Long): ResponseEntity<List<EmployeeDto>>
    fun getEmployeesByName(firstname: String, lastname: String): ResponseEntity<List<EmployeeDto>>

    fun createEmployee(employeeDto: EmployeeDto): ResponseEntity<EmployeeDto>
    fun updateEmployee(employeeDto: EmployeeDto): ResponseEntity<EmployeeDto>
    fun deleteEmployee(id: Long)

}