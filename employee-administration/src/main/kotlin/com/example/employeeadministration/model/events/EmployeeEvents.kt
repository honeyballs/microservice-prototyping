package com.example.employeeadministration.model.events

import com.example.employeeadministration.model.Employee

/**
 * Collection of relevant domain events and compensations for employees.
 */

class EmployeeCreatedCompensation(val employeeId: Long): CompensatingAction(CompensatingActionType.DELETE)
class EmployeeCreatedEvent(val employee: Employee, compensatingAction: EmployeeCreatedCompensation): DomainEvent(compensatingAction)

class EmployeeSwitchedDepartmentCompensation(val employeeId: Long, val departmentId: Long): CompensatingAction(CompensatingActionType.UPDATE)
class EmployeeSwitchedDepartmentEvent(val employeeId: Long, val departmentId: Long, compensatingAction: EmployeeSwitchedDepartmentCompensation): DomainEvent(compensatingAction)

class EmployeeChangedJobPositionCompensation(val employeeId: Long, val positionId: Long): CompensatingAction(CompensatingActionType.UPDATE)
class EmployeeChangedJobPositionEvent(val employeeId: Long , val positionId: Long, compensatingAction: EmployeeChangedJobPositionCompensation): DomainEvent(compensatingAction)

class EmployeeChangedNameCompensation(val employeeId: Long, val firstname: String, val lastname: String, val mail: String): CompensatingAction(CompensatingActionType.UPDATE)
class EmployeeChangedNameEvent(val employeeId: Long, val firstname: String, val lastname: String, val mail: String, compensatingAction: EmployeeChangedNameCompensation): DomainEvent(compensatingAction)

class EmployeeDeletedCompensation(val employeeId: Long): CompensatingAction(CompensatingActionType.CREATE)
class EmployeeDeletedEvent(val employeeId: Long, compensatingAction: EmployeeDeletedCompensation): DomainEvent(compensatingAction)

