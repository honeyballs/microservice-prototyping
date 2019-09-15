package com.example.employeeadministration.controllers

import com.example.employeeadministration.model.*
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

    fun employeeChangesName(id: Long, firstname: String, lastname: String): ResponseEntity<EmployeeDto>
    fun employeeMoves(id: Long, address: Address): ResponseEntity<EmployeeDto>
    fun employeeSwitchesBankDetails(id: Long, details: BankDetails): ResponseEntity<EmployeeDto>
    fun employeeReceivesRaise(id: Long, amount: BigDecimal): ResponseEntity<EmployeeDto>
    fun employeeMovesToDepartment(id: Long, departmentId: Long): ResponseEntity<EmployeeDto>
    fun employeeReceivesNewPosition(id: Long, positionId: Long, newSalary: BigDecimal): ResponseEntity<EmployeeDto>

}