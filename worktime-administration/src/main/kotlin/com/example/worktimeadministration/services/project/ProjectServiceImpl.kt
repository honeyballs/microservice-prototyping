package com.example.worktimeadministration.services.project

import com.example.worktimeadministration.model.aggregates.project.Project
import com.example.worktimeadministration.model.dto.project.ProjectDto
import com.example.worktimeadministration.services.MappingService
import org.springframework.stereotype.Service

@Service
class ProjectServiceImpl: ProjectService {

    override fun mapEntityToDto(entity: Project): ProjectDto {
        return ProjectDto(
                entity.projectId,
                entity.name,
                entity.description,
                entity.startDate,
                entity.projectedEndDate,
                entity.endDate,
                entity.employees.map { it.employeeId }.toSet()
        )
    }

    override fun mapDtoToEntity(dto: ProjectDto): Project {
        TODO("Not Implemented because reverse mapping should never occur. No properties need to be altered")
    }

    override fun mapEntitiesToDtos(entities: List<Project>): List<ProjectDto> {
        return entities.map { mapEntityToDto(it) }
    }

    override fun mapDtosToEntities(dtos: List<ProjectDto>): List<Project> {
        TODO("Not Implemented because reverse mapping should never occur. No properties need to be altered")
    }
}