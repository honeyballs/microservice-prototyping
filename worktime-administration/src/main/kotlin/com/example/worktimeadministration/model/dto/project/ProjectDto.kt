package com.example.worktimeadministration.model.dto.project

import com.example.worktimeadministration.configurations.datePattern
import com.example.worktimeadministration.configurations.dateTimePattern
import com.example.worktimeadministration.model.dto.employee.EmployeeDto
import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDate

class ProjectDto(
        val id: Long,
        val name: String,
        val description: String,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = datePattern) val startDate: LocalDate,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = datePattern) val projectedEndDate: LocalDate,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = datePattern) val endDate: LocalDate?,
        val employees: Set<Long>
)