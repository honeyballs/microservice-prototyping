package com.example.employeeadministration.controllers

import com.example.employeeadministration.model.dto.DepartmentDto
import com.example.employeeadministration.repositories.DepartmentRepository
import com.example.employeeadministration.services.DepartmentService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

const val departmentUrl = "department"

@RestController
class DepartmentControllerImpl(val departmentService: DepartmentService, val departmentRepository: DepartmentRepository): DepartmentController {

    @GetMapping(departmentUrl)
    override fun getAllDepartments(): ResponseEntity<List<DepartmentDto>> {
        return ok(departmentService.getAllDepartments())
    }

    @GetMapping("$departmentUrl/{id}")
    override fun getDepartmentById(@PathVariable("id") id: Long): ResponseEntity<DepartmentDto> {
        try {
            return ok(departmentService.getDepartmentById(id))
        } catch (e: Exception) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not find department using the given id")
        }
    }

    @PostMapping(departmentUrl)
    override fun createDepartment(@RequestBody departmentDto: DepartmentDto): ResponseEntity<DepartmentDto> {
        return ok(departmentService.createDepartmentUniquely(departmentDto))
    }

    @PutMapping(departmentUrl)
    override fun updateDepartment(@RequestBody departmentDto: DepartmentDto): ResponseEntity<DepartmentDto> {
        try {
            return ok(departmentService.updateDepartment(departmentDto))
        } catch (ex: Exception) {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong when updating the department", ex)
        }
    }

    @DeleteMapping("$departmentUrl/{id}")
    override fun deleteDepartment(@PathVariable("id") id: Long) {
        try {
            departmentService.deleteDepartment(id)
        } catch (e: Exception) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message)
        }
    }
}