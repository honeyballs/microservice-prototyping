package com.example.employeeadministration.controllers

import com.example.employeeadministration.model.DepartmentDto
import org.springframework.http.ResponseEntity

interface DepartmentController {

    fun getAllDepartments(): ResponseEntity<List<DepartmentDto>>
    fun getDepartmentById(id: Long): ResponseEntity<DepartmentDto>

    fun createDepartment(departmentDto: DepartmentDto): ResponseEntity<DepartmentDto>
    fun updateDepartmentName(departmentDto: DepartmentDto): ResponseEntity<DepartmentDto>
    fun deleteDepartment(id: Long): ResponseEntity<DepartmentDto>

}