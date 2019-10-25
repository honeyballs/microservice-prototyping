package com.example.worktimeadministration.repositories

import com.example.worktimeadministration.model.aggregates.WorktimeEntry
import com.example.worktimeadministration.model.aggregates.employee.Employee
import org.springframework.data.jpa.repository.JpaRepository

interface WorktimeEntryRepository: JpaRepository<WorktimeEntry, Long> {

    fun findWorktimeEntriesByProjectProjectId(id: Long): List<WorktimeEntry>
    fun findWorktimeEntriesByEmployeeEmployeeId(id: Long): List<WorktimeEntry>
    fun findWorktimeEntriesByProjectProjectIdAndEmployeeEmployeeId(projectId: Long, employeeId: Long): List<WorktimeEntry>

}