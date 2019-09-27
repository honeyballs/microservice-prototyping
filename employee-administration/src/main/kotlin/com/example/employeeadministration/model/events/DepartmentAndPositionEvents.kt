package com.example.employeeadministration.model.events

import com.example.employeeadministration.model.Department
import com.example.employeeadministration.model.Position
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Collection of relevant domain events and compensations for departments and job positions.
 */

class DepartmentCreatedCompensation(val departmentId: Long): CompensatingAction(CompensatingActionType.DELETE)
class DepartmentCreatedEvent constructor(val departmentId: Long, val name: String, compensatingAction: DepartmentCreatedCompensation): DomainEvent(compensatingAction)

class DepartmentChangedNameCompensation(val oldDepartment: Department): CompensatingAction(CompensatingActionType.UPDATE)
class DepartmentChangedNameEvent(val departmentId: Long, val name: String, compensatingAction: DepartmentChangedNameCompensation): DomainEvent(compensatingAction)

class DepartmentDeletedCompensation(val departmentId: Long): CompensatingAction(CompensatingActionType.CREATE)
class DepartmentDeletedEvent(val departmentId: Long, compensatingAction: DepartmentDeletedCompensation): DomainEvent(compensatingAction)

class PositionCreatedCompensation(val positionId: Long): CompensatingAction(CompensatingActionType.DELETE)
class PositionCreatedEvent(val positionId: Long, val title: String, compensatingAction: PositionCreatedCompensation): DomainEvent(compensatingAction)

class PositionChangedTitleCompensation(val positionId: Long, val title: String): CompensatingAction(CompensatingActionType.UPDATE)
class PositionChangedTitleEvent(val positionId: Long, val title: String, compensatingAction: PositionChangedTitleCompensation): DomainEvent(compensatingAction)

class PositionDeletedCompensation(val positionId: Long): CompensatingAction(CompensatingActionType.CREATE)
class PositionDeletedEvent(val positionId: Long, compensatingAction: PositionDeletedCompensation): DomainEvent(compensatingAction)