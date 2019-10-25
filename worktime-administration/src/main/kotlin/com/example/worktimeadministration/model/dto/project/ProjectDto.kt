package com.example.worktimeadministration.model.dto.project

import com.example.worktimeadministration.model.dto.employee.EmployeeDto
import java.time.LocalDate

class ProjectDto(
        val id: Long,
        val name: String,
        val description: String,
        val startDate: LocalDate,
        val projectedEndDate: LocalDate,
        val endDate: LocalDate?,
        val employees: Set<Long>
)