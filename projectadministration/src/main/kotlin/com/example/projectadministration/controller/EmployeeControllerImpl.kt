package com.example.projectadministration.controller

import com.example.projectadministration.model.dto.EmployeeDto
import com.example.projectadministration.repositories.ProjectRepository
import com.example.projectadministration.repositories.employee.EmployeeRepository
import com.example.projectadministration.services.employee.EmployeeService
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
        val employee = employeeRepository.findByEmployeeIdAndDeletedFalse(id).map { employeeService.mapEntityToDto(it) }.orElseThrow {
            ResponseStatusException(HttpStatus.BAD_REQUEST, "No employee found")
        }
        return ok(employee)
    }

    @GetMapping("$employeeUrl/department/{name}")
    override fun getAllEmployeesInDepartment(@PathVariable("name") name: String): ResponseEntity<List<EmployeeDto>> {
        return ok(employeeService.mapEntitiesToDtos(employeeRepository.findAllByDepartmentNameAndDeletedFalse(name)))
    }

    @GetMapping("$employeeUrl/position/{title}")
    override fun getAllEmployeesOfPosition(@PathVariable("title") title: String): ResponseEntity<List<EmployeeDto>> {
        return ok(employeeService.mapEntitiesToDtos(employeeRepository.findAllByPositionTitleAndDeletedFalse(title)))
    }

    @GetMapping("$employeeUrl/project/{id}")
    override fun getEmployeesOfProject(@PathVariable("id") projectId: Long): ResponseEntity<List<EmployeeDto>> {
        val employees = projectRepository.findById(projectId).map { employeeService.mapEntitiesToDtos(it.employees.toList()) }.orElseThrow {
            ResponseStatusException(HttpStatus.BAD_REQUEST, "No project found")
        }
        return ok(employees)
    }
}