package com.example.projectadministration.controller

import com.example.projectadministration.model.dto.ProjectDto
import com.example.projectadministration.repositories.ProjectRepository
import com.example.projectadministration.repositories.employee.EmployeeRepository
import com.example.projectadministration.services.ProjectService
import com.example.projectadministration.services.employee.EmployeeService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

const val projectUrl = "project"

@RestController
class ProjectControllerImpl(
        val projectRepository: ProjectRepository,
        val projectService: ProjectService,
        val employeeRepository: EmployeeRepository
) : ProjectController {

    @GetMapping(projectUrl)
    override fun getAllProjects(): ResponseEntity<List<ProjectDto>> {
        return ResponseEntity.ok(projectService.mapEntitiesToDtos(projectRepository.getAllByDeletedFalse()))
    }

    @GetMapping("$projectUrl/{id}")
    override fun getProjectById(@PathVariable("id") id: Long): ResponseEntity<ProjectDto> {
        val project = projectRepository.getByIdAndDeletedFalse(id).map { projectService.mapEntityToDto(it) }.orElseThrow {
            ResponseStatusException(HttpStatus.BAD_REQUEST, "No customer found")
        }
        return ResponseEntity.ok(project)
    }

    @GetMapping("$projectUrl/customer/{id}")
    override fun getProjectsOfCustomer(@PathVariable("id") customerId: Long): ResponseEntity<List<ProjectDto>> {
        return ResponseEntity.ok(projectService.mapEntitiesToDtos(projectRepository.getAllByCustomerIdAndDeletedFalse(customerId)))
    }

    @GetMapping("$projectUrl/employee/{id}")
    override fun getProjectsOfEmployee(@PathVariable("id") employeeId: Long): ResponseEntity<List<ProjectDto>> {
        val projects = employeeRepository.findByEmployeeIdAndDeletedFalse(employeeId).map {
            projectService.mapEntitiesToDtos(projectRepository.getAllByEmployeesContainingAndDeletedFalse(it))
        }.orElseThrow {
            ResponseStatusException(HttpStatus.BAD_REQUEST, "No employee found")
        }
        return ResponseEntity.ok(projects)
    }

    @PostMapping(projectUrl)
    override fun createProject(@RequestBody projectDto: ProjectDto): ResponseEntity<ProjectDto> {
        try {
            return ok(projectService.createProject(projectDto))
        } catch (ex: Exception) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Project could not be created", ex)
        }
    }

    @PutMapping(projectUrl)
    override fun updateProject(@RequestBody projectDto: ProjectDto): ResponseEntity<ProjectDto> {
        try {
            return ok(projectService.updateProject(projectDto))
        } catch (ex: Exception) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Project could not be updated", ex)
        }
    }

    @DeleteMapping("$projectUrl/{id}")
    override fun deleteProject(@PathVariable("id") id: Long) {
        try {
            projectService.deleteProject(id)
        } catch (ex: Exception) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Project could not be deleted", ex)
        }
    }
}