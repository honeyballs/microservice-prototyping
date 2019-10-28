package com.example.projectadministration.controller

import com.example.projectadministration.model.dto.EmployeeDto
import org.springframework.http.ResponseEntity

interface EmployeeController {

    fun getAllEmployees(): ResponseEntity<List<EmployeeDto>>
    fun getEmployeeById(id: Long): ResponseEntity<EmployeeDto>
    fun getAllEmployeesInDepartment(name: String): ResponseEntity<List<EmployeeDto>>
    fun getAllEmployeesOfPosition(title: String): ResponseEntity<List<EmployeeDto>>
    fun getEmployeesOfProject(projectId: Long): ResponseEntity<List<EmployeeDto>>

}