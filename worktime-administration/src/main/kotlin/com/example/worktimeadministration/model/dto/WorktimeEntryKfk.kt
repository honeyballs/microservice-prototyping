package com.example.worktimeadministration.model.dto

import com.example.worktimeadministration.model.aggregates.AggregateState
import com.example.worktimeadministration.model.aggregates.EntryType
import com.example.worktimeadministration.model.aggregates.employee.Employee
import com.example.worktimeadministration.model.aggregates.project.Project
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeName
import java.time.LocalDateTime

@JsonTypeName("worktimeKfk")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)
class WorktimeEntryKfk(
        override val id: Long,
        var startTime: LocalDateTime,
        var endTime: LocalDateTime,
        var pauseTimeInMinutes: Int = 0,
        val project: Long,
        val employee: Long,
        var description: String,
        var type: EntryType,
        var deleted: Boolean = false,
        var state: AggregateState
): BaseKfkDto