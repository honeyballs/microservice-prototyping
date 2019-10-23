package com.example.employeeadministration.model.dto

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeName("kfkDto")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)
@JsonSubTypes(
        JsonSubTypes.Type(value = DepartmentKfk::class, name = "departmentKfk"),
        JsonSubTypes.Type(value = PositionKfk::class, name = "positionKfk"),
        JsonSubTypes.Type(value = EmployeeKfk::class, name = "employeeKfk")
)
interface BaseKfkDto {
    val id: Long
}