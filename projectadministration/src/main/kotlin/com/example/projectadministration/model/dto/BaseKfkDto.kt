package com.example.projectadministration.model.dto

import com.example.projectadministration.model.dto.employee.DepartmentKfk
import com.example.projectadministration.model.dto.employee.EmployeeKfk
import com.example.projectadministration.model.dto.employee.PositionKfk
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeName("kfkDto")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)
@JsonSubTypes(
        JsonSubTypes.Type(value = DepartmentKfk::class, name = "departmentKfk"),
        JsonSubTypes.Type(value = PositionKfk::class, name = "positionKfk"),
        JsonSubTypes.Type(value = EmployeeKfk::class, name = "employeeKfk"),
        JsonSubTypes.Type(value = ProjectKfk::class, name = "projectKfk"),
        JsonSubTypes.Type(value = CustomerKfk::class, name = "customerKfk")
)
interface BaseKfkDto {
    val id: Long
}