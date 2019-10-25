package com.example.worktimeadministration.services.project

import com.example.worktimeadministration.model.aggregates.project.Project
import com.example.worktimeadministration.model.dto.project.ProjectDto
import com.example.worktimeadministration.services.MappingService

interface ProjectService: MappingService<Project, ProjectDto> {
}