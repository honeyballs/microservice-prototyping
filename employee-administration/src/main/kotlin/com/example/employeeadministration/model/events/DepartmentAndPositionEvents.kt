package com.example.employeeadministration.model.events

import com.example.employeeadministration.model.Department
import com.example.employeeadministration.model.Position

/**
 * Collection of relevant domain events for departments and job positions.
 */

data class DepartmentCreatedEvent(val department: Department): DomainEvent()
data class DepartmentChangedNameEvent(val department: Department): DomainEvent()
data class DepartmentDeletedEvent(val departmentId: Long): DomainEvent()

data class PositionCreatedEvent(val position: Position): DomainEvent()
class PositionChangedTitleEvent(val positionId: Long, val title: String): DomainEvent()
class PositionDeletedEvent(val positionId: Long): DomainEvent()