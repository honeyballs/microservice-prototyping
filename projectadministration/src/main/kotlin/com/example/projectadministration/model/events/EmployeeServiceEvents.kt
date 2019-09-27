package com.example.projectadministration.model.events

import com.example.projectadministration.model.employee.Department
import com.example.projectadministration.model.employee.Employee
import com.example.projectadministration.model.employee.Position
import java.math.BigDecimal

class EmployeeCreatedCompensation(val employeeId: Long): CompensatingAction(CompensatingActionType.DELETE)
class EmployeeCreatedEvent(val employeeId: Long, val firstname: String, val lastname: String, val departmentId: Long, val positionId: Long, val mail: String, val hourlyRate: BigDecimal, compensatingAction: EmployeeCreatedCompensation): DomainEvent(compensatingAction)

class EmployeeSwitchedDepartmentCompensation(val employeeId: Long, val departmentId: Long): CompensatingAction(CompensatingActionType.UPDATE)
class EmployeeSwitchedDepartmentEvent(val employeeId: Long, val departmentId: Long, compensatingAction: EmployeeSwitchedDepartmentCompensation): DomainEvent(compensatingAction)

class EmployeeChangedJobPositionCompensation(val employeeId: Long, val positionId: Long, val oldWage: BigDecimal): CompensatingAction(CompensatingActionType.UPDATE)
class EmployeeChangedJobPositionEvent(val employeeId: Long , val positionId: Long, compensatingAction: EmployeeChangedJobPositionCompensation): DomainEvent(compensatingAction)

class EmployeeChangedNameCompensation(val employeeId: Long, val firstname: String, val lastname: String, val mail: String): CompensatingAction(CompensatingActionType.UPDATE)
class EmployeeChangedNameEvent(val employeeId: Long, val firstname: String, val lastname: String, val mail: String, compensatingAction: EmployeeChangedNameCompensation): DomainEvent(compensatingAction)

class EmployeeDeletedCompensation(val employeeId: Long): CompensatingAction(CompensatingActionType.CREATE)
class EmployeeDeletedEvent(val employeeId: Long, compensatingAction: EmployeeDeletedCompensation): DomainEvent(compensatingAction)

class DepartmentCreatedCompensation(val departmentId: Long): CompensatingAction(CompensatingActionType.DELETE)
class DepartmentCreatedEvent constructor(val departmentId: Long, val name: String, compensatingAction: DepartmentCreatedCompensation): DomainEvent(compensatingAction)

class DepartmentChangedNameCompensation(val departmentId: Long, val oldName: String): CompensatingAction(CompensatingActionType.UPDATE)
class DepartmentChangedNameEvent(val departmentId: Long, val name: String, compensatingAction: DepartmentChangedNameCompensation): DomainEvent(compensatingAction)

class DepartmentDeletedCompensation(val departmentId: Long): CompensatingAction(CompensatingActionType.CREATE)
class DepartmentDeletedEvent(val departmentId: Long, compensatingAction: DepartmentDeletedCompensation): DomainEvent(compensatingAction)

class PositionCreatedCompensation(val positionId: Long): CompensatingAction(CompensatingActionType.DELETE)
class PositionCreatedEvent(val positionId: Long, val title: String, compensatingAction: PositionCreatedCompensation): DomainEvent(compensatingAction)

class PositionChangedTitleCompensation(val positionId: Long, val title: String): CompensatingAction(CompensatingActionType.UPDATE)
class PositionChangedTitleEvent(val positionId: Long, val title: String, compensatingAction: PositionChangedTitleCompensation): DomainEvent(compensatingAction)

class PositionDeletedCompensation(val positionId: Long): CompensatingAction(CompensatingActionType.CREATE)
class PositionDeletedEvent(val positionId: Long, compensatingAction: PositionDeletedCompensation): DomainEvent(compensatingAction)