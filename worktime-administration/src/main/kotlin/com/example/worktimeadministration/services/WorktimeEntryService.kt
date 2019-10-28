package com.example.worktimeadministration.services

import com.example.worktimeadministration.model.aggregates.WorktimeEntry
import com.example.worktimeadministration.model.aggregates.employee.Employee
import com.example.worktimeadministration.model.aggregates.project.Project
import com.example.worktimeadministration.model.dto.WorktimeEntryDto
import java.time.LocalDateTime

interface WorktimeEntryService : MappingService<WorktimeEntry, WorktimeEntryDto>, EventProducingPersistenceService<WorktimeEntry> {

    fun createEntry(worktimeEntryDto: WorktimeEntryDto): WorktimeEntryDto
    fun updateWorktimeEntry(worktimeEntryDto: WorktimeEntryDto): WorktimeEntryDto
    fun deleteEntry(id: Long)

}