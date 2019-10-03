package com.example.projectadministration.services

import com.example.projectadministration.model.Project
import com.example.projectadministration.model.ProjectDto
import org.springframework.stereotype.Service

@Service
interface ProjectService: MappingService<Project, ProjectDto>, EventProducingPersistenceService<Project> {


}