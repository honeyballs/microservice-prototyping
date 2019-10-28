package com.example.worktimeadministration.services

import com.example.worktimeadministration.model.aggregates.AggregateState
import com.example.worktimeadministration.model.aggregates.EntryType
import com.example.worktimeadministration.model.aggregates.WorktimeEntry
import com.example.worktimeadministration.model.aggregates.employee.Employee
import com.example.worktimeadministration.model.aggregates.project.Project
import com.example.worktimeadministration.model.dto.WorktimeEntryDto
import com.example.worktimeadministration.repositories.WorktimeEntryRepository
import com.example.worktimeadministration.repositories.employee.EmployeeRepository
import com.example.worktimeadministration.repositories.project.ProjectRepository
import com.example.worktimeadministration.services.employee.EmployeeService
import com.example.worktimeadministration.services.project.ProjectService
import org.springframework.stereotype.Service
import org.springframework.transaction.UnexpectedRollbackException
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class WorktimeEntryServiceImpl(
        val projectService: ProjectService,
        val employeeService: EmployeeService,
        val worktimeEntryRepository: WorktimeEntryRepository,
        val projectRepository: ProjectRepository,
        val employeeRepository: EmployeeRepository,
        val sagaService: SagaService,
        val eventProducer: EventProducer
) : WorktimeEntryService {

    /**
     * Collects multiple Updates on an aggregate.
     */
    @Throws(Exception::class)
    override fun updateWorktimeEntry(worktimeEntryDto: WorktimeEntryDto): WorktimeEntryDto {
        val entryToUpdate = worktimeEntryRepository.findById(worktimeEntryDto.id!!).orElseThrow()
        if (entryToUpdate.startTime != worktimeEntryDto.startTime) {
            entryToUpdate.adjustStartTime(worktimeEntryDto.startTime)
        }
        if (entryToUpdate.endTime != worktimeEntryDto.endTime) {
            entryToUpdate.adjustEndTime(worktimeEntryDto.endTime)
        }
        if (entryToUpdate.pauseTimeInMinutes != worktimeEntryDto.pauseTimeInMinutes) {
            entryToUpdate.adjustPauseTime(worktimeEntryDto.pauseTimeInMinutes)
        }
        if (entryToUpdate.description != worktimeEntryDto.description) {
            entryToUpdate.changeDescription(worktimeEntryDto.description)
        }
        if (entryToUpdate.project.projectId != worktimeEntryDto.project.id) {
            entryToUpdate.project = projectRepository.findByProjectIdAndDeletedFalse(worktimeEntryDto.project.id).orElseThrow()
        }
        return mapEntityToDto(persistWithEvents(entryToUpdate))
    }

    @Throws(Exception::class)
    override fun createEntry(worktimeEntryDto: WorktimeEntryDto): WorktimeEntryDto {
        val entry = mapDtoToEntity(worktimeEntryDto)
        if (entry.type == EntryType.VACATION) {
            entry.employee.usedVacationHours -= entry.calculateTimespan(entry.startTime, entry.endTime)
            employeeRepository.save(entry.employee)
        }
        return mapEntityToDto(persistWithEvents(entry))
    }

    @Throws(Exception::class)
    override fun deleteEntry(id: Long) {
        val entry = worktimeEntryRepository.findById(id).orElseThrow()
        entry.deleteEntry()
        persistWithEvents(entry)
    }

    @Transactional
    override fun persistWithEvents(aggregate: WorktimeEntry): WorktimeEntry {
        var agg: WorktimeEntry? = null
        try {
            // If id is null this is a newly created aggregate
            if (aggregate.id == null) {
                agg = worktimeEntryRepository.save(aggregate)
                agg.created()
            } else {
                agg = worktimeEntryRepository.save(aggregate)
            }

            // If services must send response events to changes in the aggregate we create a saga
            var canBeMadeActive = true
            aggregate.events()!!.second.forEach {
                val responseEvents = getRequiredSuccessEvents(it.type)
                if (responseEvents != "") {
                    sagaService.createSagaOfEvent(it, agg.id!!, responseEvents)
                    canBeMadeActive = false
                }
            }

            // If no saga was necessary the aggregate can immediately become active
            if (canBeMadeActive) {
                agg.state = AggregateState.ACTIVE
                worktimeEntryRepository.save(agg)
            }

            // Send all events
            eventProducer.sendEventsOfAggregate(aggregate)

        } catch (rollback: UnexpectedRollbackException) {
            rollback.printStackTrace()
        } catch (ex: Exception) {
            ex.printStackTrace()
        } finally {
            return agg ?: aggregate
        }
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
        val project = projectRepository.findByProjectIdAndDeletedFalse(dto.project.id).orElseThrow()
        val employee = employeeRepository.findByEmployeeIdAndDeletedFalse(dto.employee.id).orElseThrow()
        return WorktimeEntry(dto.id, dto.startTime, dto.endTime, dto.pauseTimeInMinutes, project, employee, dto.description, dto.type)
    }

    override fun mapEntitiesToDtos(entities: List<WorktimeEntry>): List<WorktimeEntryDto> {
        return entities.map { mapEntityToDto(it) }
    }

    override fun mapDtosToEntities(dtos: List<WorktimeEntryDto>): List<WorktimeEntry> {
        return dtos.map { mapDtoToEntity(it) }
    }


}