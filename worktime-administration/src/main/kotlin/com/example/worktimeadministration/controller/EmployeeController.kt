package com.example.worktimeadministration.controller

import com.example.worktimeadministration.model.dto.employee.EmployeeDto
import org.springframework.http.ResponseEntity

interface EmployeeController {

    fun getAllEmployees(): ResponseEntity<List<EmployeeDto>>
    fun getEmployeeById(id: Long): ResponseEntity<EmployeeDto>
    fun getEmployeesOfProject(projectId: Long): ResponseEntity<List<EmployeeDto>>

}