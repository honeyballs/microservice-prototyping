package com.example.worktimeadministration.model.dto

import com.example.worktimeadministration.configurations.dateTimePattern
import com.example.worktimeadministration.model.aggregates.AggregateState
import com.example.worktimeadministration.model.aggregates.EntryType
import com.example.worktimeadministration.model.aggregates.employee.Employee
import com.example.worktimeadministration.model.dto.employee.EmployeeDto
import com.example.worktimeadministration.model.dto.project.ProjectDto
import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDateTime

class WorktimeEntryDto(
        val id: Long?,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = dateTimePattern) var startTime: LocalDateTime,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = dateTimePattern) var endTime: LocalDateTime,
        var pauseTimeInMinutes: Int = 0,
        val project: ProjectDto,
        val employee: EmployeeDto,
        var description: String,
        var type: EntryType
)