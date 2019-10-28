package com.example.worktimeadministration.controller

import com.example.worktimeadministration.model.dto.project.ProjectDto
import org.apache.catalina.connector.Response
import org.springframework.http.ResponseEntity

interface ProjectController {

    fun getAllProjects(): ResponseEntity<List<ProjectDto>>
    fun getProjectById(id: Long): ResponseEntity<ProjectDto>
    fun getProjectsOfEmployee(employeeId: Long): ResponseEntity<List<ProjectDto>>

}