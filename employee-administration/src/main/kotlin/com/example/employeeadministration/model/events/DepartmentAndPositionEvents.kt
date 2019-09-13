package com.example.employeeadministration.model.events

import com.example.employeeadministration.model.Department
import com.example.employeeadministration.model.Position

enum class DepartmentActions(name: String) {
    CREATED_DEPARTMENT("CREATED_DEPARTMENT"),
    CHANGED_DEPARTMENT_NAME("CHANGED_DEPARTMENT_NAME"),
    DELETED_DEPARTMENT("DELETED_DEPARTMENT"),
}

class CreatedDepartmentEvent(department: Department): DomainEvent(DepartmentActions.CREATED_DEPARTMENT.name, department)
class DepartmentChangedNameEvent(department: Department): DomainEvent(DepartmentActions.CHANGED_DEPARTMENT_NAME.name, department)
class DepartmentDeletedEvent(department: Department): DomainEvent(DepartmentActions.DELETED_DEPARTMENT.name, department)

enum class PositionActions(name: String) {
    CREATED_POSITION("CREATED_POSITION"),
    POSITION_CHANGED_TITLE("POSITION_CHANGED_TITLE"),
    POSITION_CHANGED_RANGE("POSITION_CHANGED_RANGE"),
    DELETED_POSITION("DELETED_POSITION"),
}

class CreatedPositionEvent(position: Position): DomainEvent(PositionActions.CREATED_POSITION.name, position)
class PositionChangedTitleEvent(position: Position): DomainEvent(PositionActions.POSITION_CHANGED_TITLE.name, position)
class PositionChangedRangeEvent(position: Position): DomainEvent(PositionActions.POSITION_CHANGED_RANGE.name, position)
class PositionDeletedEvent(position: Position): DomainEvent(PositionActions.DELETED_POSITION.name, position)