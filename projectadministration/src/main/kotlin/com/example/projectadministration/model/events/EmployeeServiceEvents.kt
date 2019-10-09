package com.example.projectadministration.model.events

import com.example.projectadministration.model.employee.*
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeName
import java.math.BigDecimal

@JsonTypeName("employeeCompensation")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)
class EmployeeCompensation(val employee: EmployeeKfk, type: EventType): CompensatingAction(type)

@JsonTypeName("employeeEvent")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)
class EmployeeEvent(val employee: EmployeeKfk, compensatingAction: EmployeeCompensation, type: EventType): DomainEvent(compensatingAction, type)


@JsonTypeName("departmentCompensation")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)
class DepartmentCompensation(val department: DepartmentKfk, type: EventType): CompensatingAction(type)

@JsonTypeName("departmentEvent")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)
class DepartmentEvent(val department: DepartmentKfk, compensatingAction: DepartmentCompensation, type: EventType): DomainEvent(compensatingAction, type)


@JsonTypeName("positionCompensation")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)
class PositionCompensation(val position: PositionKfk, type: EventType): CompensatingAction(type)

@JsonTypeName("positionEvent")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)
class PositionEvent(val position: PositionKfk, compensatingAction: PositionCompensation, type: EventType): DomainEvent(compensatingAction, type)