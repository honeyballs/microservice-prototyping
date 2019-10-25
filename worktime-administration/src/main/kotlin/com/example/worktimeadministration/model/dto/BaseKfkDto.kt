package com.example.worktimeadministration.model.dto


import com.example.worktimeadministration.model.dto.employee.EmployeeKfk
import com.example.worktimeadministration.model.dto.project.ProjectKfk
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeName("kfkDto")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)
@JsonSubTypes(
        JsonSubTypes.Type(value = EmployeeKfk::class, name = "employeeKfk"),
        JsonSubTypes.Type(value = ProjectKfk::class, name = "projectKfk"),
        JsonSubTypes.Type(value = WorktimeEntryKfk::class, name = "worktimeKfk")
)
interface BaseKfkDto {
    val id: Long
}