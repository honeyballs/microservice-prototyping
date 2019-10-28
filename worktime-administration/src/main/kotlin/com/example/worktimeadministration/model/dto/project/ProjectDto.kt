package com.example.worktimeadministration.model.dto.project

import com.example.worktimeadministration.configurations.dateTimePattern
import com.example.worktimeadministration.model.dto.employee.EmployeeDto
import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDate

class ProjectDto(
        val id: Long,
        val name: String,
        val description: String,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = dateTimePattern) val startDate: LocalDate,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = dateTimePattern) val projectedEndDate: LocalDate,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = dateTimePattern) val endDate: LocalDate?,
        val employees: Set<Long>
)