package com.example.worktimeadministration.model.dto

import com.example.worktimeadministration.configurations.dateTimePattern
import com.example.worktimeadministration.model.aggregates.AggregateState
import com.example.worktimeadministration.model.aggregates.EntryType
import com.example.worktimeadministration.model.aggregates.employee.Employee
import com.example.worktimeadministration.model.dto.employee.EmployeeDto
import com.example.worktimeadministration.model.dto.project.ProjectDto
import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

@JsonIgnoreProperties(value = ["state"], allowGetters = true)
class WorktimeEntryDto(
        val id: Long?,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = dateTimePattern) var startTime: LocalDateTime,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = dateTimePattern) var endTime: LocalDateTime,
        var pauseTimeInMinutes: Int = 0,
        val project: ProjectDto,
        val employee: EmployeeDto,
        var description: String,
        var type: EntryType,
        @JsonProperty("state") val state: AggregateState?
)