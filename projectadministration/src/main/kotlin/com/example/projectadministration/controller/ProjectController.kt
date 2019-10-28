package com.example.projectadministration.controller

import com.example.projectadministration.model.dto.ProjectDto
import org.springframework.http.ResponseEntity

interface ProjectController {

    fun getAllProjects(): ResponseEntity<List<ProjectDto>>
    fun getProjectById(id: Long): ResponseEntity<ProjectDto>
    fun getProjectsOfCustomer(customerId: Long): ResponseEntity<List<ProjectDto>>
    fun getProjectsOfEmployee(employeeId: Long): ResponseEntity<List<ProjectDto>>

    fun createProject(projectDto: ProjectDto): ResponseEntity<ProjectDto>
    fun updateProject(projectDto: ProjectDto): ResponseEntity<ProjectDto>
    fun deleteProject(id: Long)

}