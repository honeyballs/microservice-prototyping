package com.example.projectadministration.services

import com.example.projectadministration.model.aggregates.Project
import com.example.projectadministration.model.dto.ProjectDto
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime

@Service
interface ProjectService: MappingService<Project, ProjectDto>, EventProducingPersistenceService<Project> {

    fun getAllProjects(): List<ProjectDto>
    fun getProjectById(id: Long): ProjectDto
    fun getProjectsOfCustomer(customerId: Long): List<ProjectDto>
    fun getProjectsOfEmployee(employeeId: Long): List<ProjectDto>

    fun createProject(projectDto: ProjectDto): ProjectDto
    fun updateProject(projectDto: ProjectDto): ProjectDto
    fun finishProject(id: Long, endDate: LocalDate): ProjectDto
    fun deleteProject(id: Long)

}