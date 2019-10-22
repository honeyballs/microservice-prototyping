package com.example.projectadministration.services

import com.example.projectadministration.model.aggregates.Project
import com.example.projectadministration.model.dto.ProjectDto
import org.springframework.stereotype.Service

@Service
interface ProjectService: MappingService<Project, ProjectDto>, EventProducingPersistenceService<Project> {


}