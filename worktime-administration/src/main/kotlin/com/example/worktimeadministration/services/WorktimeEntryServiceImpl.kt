package com.example.worktimeadministration.services

import com.example.worktimeadministration.model.aggregates.WorktimeEntry
import com.example.worktimeadministration.model.dto.WorktimeEntryDto
import com.example.worktimeadministration.repositories.WorktimeEntryRepository
import com.example.worktimeadministration.repositories.employee.EmployeeRepository
import com.example.worktimeadministration.repositories.project.ProjectRepository
import com.example.worktimeadministration.services.employee.EmployeeService
import com.example.worktimeadministration.services.project.ProjectService
import org.springframework.stereotype.Service

@Service
class WorktimeEntryServiceImpl(
        val projectService: ProjectService,
        val employeeService: EmployeeService,
        val worktimeEntryRepository: WorktimeEntryRepository,
        val projectRepository: ProjectRepository,
        val employeeRepository: EmployeeRepository
) : WorktimeEntryService {

    override fun persistWithEvents(aggregate: WorktimeEntry): WorktimeEntry {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun mapEntityToDto(entity: WorktimeEntry): WorktimeEntryDto {
        return WorktimeEntryDto(
                entity.id!!,
                entity.startTime,
                entity.endTime,
                entity.pauseTimeInMinutes,
                projectService.mapEntityToDto(entity.project),
                employeeService.mapEntityToDto(entity.employee),
                entity.description,
                entity.type
        )
    }

    override fun mapDtoToEntity(dto: WorktimeEntryDto): WorktimeEntry {
        val project = projectRepository.findByProjectId(dto.project.id).orElseThrow()
        val employee = employeeRepository.findByEmployeeId(dto.employee.id).orElseThrow()
        return WorktimeEntry(dto.id, dto.startTime, dto.endTime, dto.pauseTimeInMinutes, project, employee, dto.description, dto.type)
    }

    override fun mapEntitiesToDtos(entities: List<WorktimeEntry>): List<WorktimeEntryDto> {
        return entities.map { mapEntityToDto(it) }
    }

    override fun mapDtosToEntities(dtos: List<WorktimeEntryDto>): List<WorktimeEntry> {
        return dtos.map { mapDtoToEntity(it) }
    }


}