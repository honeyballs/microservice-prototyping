package com.example.employeeadministration.controllers

import com.example.employeeadministration.model.DepartmentDto
import com.example.employeeadministration.repositories.DepartmentRepository
import com.example.employeeadministration.services.DepartmentService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

const val departmentUrl = "departments"

@RestController
class DepartmentControllerImpl(val departmentService: DepartmentService, val departmentRepository: DepartmentRepository): DepartmentController {

    @GetMapping(departmentUrl)
    override fun getAllDepartments(): ResponseEntity<List<DepartmentDto>> {
        return ok(departmentRepository.findAll().map { departmentService.mapEntityToDto(it) })
    }

    @GetMapping("$departmentUrl/{id}")
    override fun getDepartmentById(@PathVariable("id") id: Long): ResponseEntity<DepartmentDto> {
        return ok(departmentRepository.getById(id).map { departmentService.mapEntityToDto(it) }.orElseThrow{
            ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not find department using the given id")
        })
    }

    @PostMapping(departmentUrl)
    override fun createDepartment(@RequestBody departmentDto: DepartmentDto): ResponseEntity<DepartmentDto> {
        return ok(departmentService.createDepartmentUniquely(departmentDto))
    }

    @PutMapping(departmentUrl)
    override fun updateDepartmentName(@RequestBody departmentDto: DepartmentDto): ResponseEntity<DepartmentDto> {
        return ok(departmentRepository.getById(departmentDto.id!!).map {
            it.renameDepartment(departmentDto.name)
            departmentService.mapEntityToDto(departmentRepository.save(it))
        }.orElseThrow {
            ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not find department to update")
        })
    }

    @DeleteMapping("$departmentUrl/{id}")
    override fun deleteDepartment(@PathVariable("id") id: Long): ResponseEntity<DepartmentDto> {
        val department = departmentRepository.getById(id).orElseThrow {
            ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not find department to delete")
        }
        departmentRepository.deleteById(id)
        return ok(departmentService.mapEntityToDto(department))
    }
}