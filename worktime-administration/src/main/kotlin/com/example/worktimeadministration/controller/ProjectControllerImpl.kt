package com.example.worktimeadministration.controller

import com.example.worktimeadministration.model.aggregates.employee.Employee
import com.example.worktimeadministration.model.aggregates.project.Project
import com.example.worktimeadministration.model.dto.project.ProjectDto
import com.example.worktimeadministration.repositories.employee.EmployeeRepository
import com.example.worktimeadministration.repositories.project.ProjectRepository
import com.example.worktimeadministration.services.project.ProjectService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

const val projectUrl = "project"

@RestController
class ProjectControllerImpl(
        val projectRepository: ProjectRepository,
        val projectService: ProjectService,
        val employeeRepository: EmployeeRepository
): ProjectController {

    @GetMapping(projectUrl)
    override fun getAllProjects(): ResponseEntity<List<ProjectDto>> {
        return ResponseEntity.ok(projectService.mapEntitiesToDtos(projectRepository.findAllByDeletedFalse()))
    }

    @GetMapping("$projectUrl/{id}")
    override fun getProjectById(@PathVariable("id") id: Long): ResponseEntity<ProjectDto> {
        try {
            return ResponseEntity.ok(projectService.mapEntityToDto(projectRepository.findByProjectIdAndDeletedFalse(id).orElseThrow()))
        } catch (ex: NoSuchElementException) {
            throw ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "No Project under the given id", ex)
        }
    }

    @GetMapping("$projectUrl/employee/{id}")
    override fun getProjectsOfEmployee(@PathVariable("id") employeeId: Long): ResponseEntity<List<ProjectDto>> {
        var employee: Employee? = null
        try {
            employee = employeeRepository.findByEmployeeIdAndDeletedFalse(employeeId).orElseThrow()
        } catch (ex: NoSuchElementException) {
            throw ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "No Employee under the given id", ex)
        }
        return ResponseEntity.ok(projectService.mapEntitiesToDtos(projectRepository.findAllByEmployeesContainsAndDeletedFalse(employee)))
    }
}