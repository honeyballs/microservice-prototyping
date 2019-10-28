package com.example.worktimeadministration.repositories

import com.example.worktimeadministration.model.aggregates.WorktimeEntry
import com.example.worktimeadministration.model.aggregates.employee.Employee
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface WorktimeEntryRepository: JpaRepository<WorktimeEntry, Long> {

    fun findAllByDeletedFalse(): List<WorktimeEntry>
    fun findByIdAndDeletedFalse(id: Long): Optional<WorktimeEntry>
    fun findAllByProjectProjectIdAndDeletedFalse(id: Long): List<WorktimeEntry>
    fun findAllByEmployeeEmployeeIdAndDeletedFalse(id: Long): List<WorktimeEntry>
    fun findAllByProjectProjectIdAndEmployeeEmployeeIdAndDeletedFalse(projectId: Long, employeeId: Long): List<WorktimeEntry>

}