package com.example.employeeadministration.model.events

import com.example.employeeadministration.model.Employee

/**
 * Collection of relevant domain events for employees.
 */

data class EmployeeCreatedEvent(val employee: Employee): DomainEvent()
data class EmployeeSwitchedDepartmentEvent(val employeeId: Long, val departmentId: Long): DomainEvent()
data class EmployeeChangedJobPositionEvent(val employeeId: Long , val positionId: Long): DomainEvent()
data class EmployeeChangedNameEvent(val employeeId: Long, val firstname: String, val lastname: String, val mail: String): DomainEvent()
data class EmployeeDeletedEvent(val employeeId: Long): DomainEvent()

