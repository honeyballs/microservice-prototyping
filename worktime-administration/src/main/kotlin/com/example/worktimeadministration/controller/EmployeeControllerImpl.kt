package com.example.worktimeadministration.controller

import com.example.worktimeadministration.model.aggregates.project.Project
import com.example.worktimeadministration.model.dto.employee.EmployeeDto
import com.example.worktimeadministration.repositories.employee.EmployeeRepository
import com.example.worktimeadministration.repositories.project.ProjectRepository
import com.example.worktimeadministration.services.employee.EmployeeService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

const val employeeUrl = "employee"

@RestController
class EmployeeControllerImpl(
        val employeeRepository: EmployeeRepository,
        val employeeService: EmployeeService,
        val projectRepository: ProjectRepository
): EmployeeController {

    @GetMapping(employeeUrl)
    override fun getAllEmployees(): ResponseEntity<List<EmployeeDto>> {
        return ok(employeeService.mapEntitiesToDtos(employeeRepository.findAllByDeletedFalse()))
    }

    @GetMapping("$employeeUrl/{id}")
    override fun getEmployeeById(@PathVariable("id") id: Long): ResponseEntity<EmployeeDto> {
        try {
            return ok(employeeService.mapEntityToDto(employeeRepository.findByEmployeeIdAndDeletedFalse(id).orElseThrow()))
        } catch (ex: NoSuchElementException) {
            throw ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "No Employee under the given id", ex)
        }
    }

    @GetMapping("$employeeUrl/project/{id}")
    override fun getEmployeesOfProject(@PathVariable("id") projectId: Long): ResponseEntity<List<EmployeeDto>> {
        var project: Project? = null
        try {
            project = projectRepository.findByProjectIdAndDeletedFalse(projectId).orElseThrow()
        } catch (ex: NoSuchElementException) {
            throw ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "No Project under the given id", ex)
        }
        return ok(employeeService.mapEntitiesToDtos(project.employees.toList()))
    }
}