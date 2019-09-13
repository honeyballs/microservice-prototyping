package com.example.employeeadministration.model.events

import com.example.employeeadministration.model.Employee

enum class EmployeeActions(action: String) {
    CREATE_EMPLOYEE("CREATE_EMPLOYEE"),
    EMPLOYEE_MOVED("EMPLOYEE_MOVED"),
    EMPLOYEE_CHANGED_BANKING("EMPLOYEE_CHANGED_BANKING"),
    EMPLOYEE_GOT_RAISE("EMPLOYEE_GOT_RAISE"),
    EMPLOYEE_SWITCHED_DEPARTMENT("EMPLOYEE_SWITCHED_DEPARTMENT"),
    EMPLOYEE_CHANGED_JOB_POSITION("EMPLOYEE_CHANGED_JOB_POSITION"),
    EMPLOYEE_CHANGED_NAME("EMPLOYEE_CHANGED_NAME"),
    EMPLOYEE_DELETED("EMPLOYEE_DELETED")
}

class CreateEmployeeEvent(employee: Employee): DomainEvent(EmployeeActions.CREATE_EMPLOYEE.name, employee)
class EmployeeMovedEvent(employee: Employee): DomainEvent(EmployeeActions.EMPLOYEE_MOVED.name, employee)
class EmployeeChangedBankingEvent(employee: Employee): DomainEvent(EmployeeActions.EMPLOYEE_CHANGED_BANKING.name, employee)
class EmployeeGotRaiseEvent(employee: Employee): DomainEvent(EmployeeActions.EMPLOYEE_GOT_RAISE.name, employee)
class EmployeeSwitchedDepartmentEvent(employee: Employee): DomainEvent(EmployeeActions.EMPLOYEE_SWITCHED_DEPARTMENT.name, employee)
class EmployeeChangedJobPositionEvent(employee: Employee): DomainEvent(EmployeeActions.EMPLOYEE_CHANGED_JOB_POSITION.name, employee)
class EmployeeChangedNameEvent(employee: Employee): DomainEvent(EmployeeActions.EMPLOYEE_CHANGED_NAME.name, employee)
class EmployeeDeletedEvent(employee: Employee): DomainEvent(EmployeeActions.EMPLOYEE_DELETED.name, employee)

