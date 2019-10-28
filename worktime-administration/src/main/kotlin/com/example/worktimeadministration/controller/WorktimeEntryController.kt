package com.example.worktimeadministration.controller

import com.example.worktimeadministration.model.dto.WorktimeEntryDto
import org.springframework.http.ResponseEntity

interface WorktimeEntryController {

    fun getAllEntriesOfEmployee(employeeId: Long): ResponseEntity<List<WorktimeEntryDto>>
    fun getAllEntriesOfProject(projectId: Long): ResponseEntity<List<WorktimeEntryDto>>
    fun getHoursOnProject(projectId: Long): ResponseEntity<Int>
    fun getAllEntriesOfEmployeeOnProject(employeeId: Long, projectId: Long): ResponseEntity<List<WorktimeEntryDto>>
    fun getAllEntries(): ResponseEntity<List<WorktimeEntryDto>>
    fun getEntryById(id: Long): ResponseEntity<WorktimeEntryDto>

    fun createEntry(worktimeEntryDto: WorktimeEntryDto): ResponseEntity<WorktimeEntryDto>
    fun updateEntry(worktimeEntryDto: WorktimeEntryDto): ResponseEntity<WorktimeEntryDto>
    fun deleteWorkTimeEntry(id: Long)

}