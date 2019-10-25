package com.example.worktimeadministration.model.dto.project

import com.example.worktimeadministration.model.aggregates.AggregateState
import com.example.worktimeadministration.model.dto.BaseKfkDto
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeName
import java.time.LocalDate

@JsonTypeName("projectKfk")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)
data class ProjectKfk(
        override val id: Long,
        val name: String,
        val description: String,
        val startDate: LocalDate,
        val projectedEndDate: LocalDate,
        val endDate: LocalDate?,
        val employees: Set<Long>,
        val deleted: Boolean,
        val state: AggregateState
): BaseKfkDto