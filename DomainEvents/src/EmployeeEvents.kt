import java.math.BigDecimal

/**
 * Collection of relevant domain events and compensations for employees.
 */

class EmployeeCreatedCompensation(val employeeId: Long, serviceName: String): CompensatingAction(CompensatingActionType.DELETE, serviceName)
class EmployeeCreatedEvent(val employeeId: Long, val firstname: String, val lastname: String, val departmentId: Long, val positionId: Long, val mail: String, val hourlyRate: BigDecimal, compensatingAction: EmployeeCreatedCompensation, serviceName: String): DomainEvent(compensatingAction, serviceName)

class EmployeeSwitchedDepartmentCompensation(val employeeId: Long, val departmentId: Long, serviceName: String): CompensatingAction(CompensatingActionType.UPDATE, serviceName)
class EmployeeSwitchedDepartmentEvent(val employeeId: Long, val departmentId: Long, compensatingAction: EmployeeSwitchedDepartmentCompensation, serviceName: String): DomainEvent(compensatingAction, serviceName)

class EmployeeChangedJobPositionCompensation(val employeeId: Long, val positionId: Long, val oldWage: BigDecimal, serviceName: String): CompensatingAction(CompensatingActionType.UPDATE, serviceName)
class EmployeeChangedJobPositionEvent(val employeeId: Long , val positionId: Long, compensatingAction: EmployeeChangedJobPositionCompensation, serviceName: String): DomainEvent(compensatingAction, serviceName)

class EmployeeChangedNameCompensation(val employeeId: Long, val firstname: String, val lastname: String, val mail: String, serviceName: String): CompensatingAction(CompensatingActionType.UPDATE, serviceName)
class EmployeeChangedNameEvent(val employeeId: Long, val firstname: String, val lastname: String, val mail: String, compensatingAction: EmployeeChangedNameCompensation, serviceName: String): DomainEvent(compensatingAction, serviceName)

class EmployeeDeletedCompensation(val employeeId: Long, serviceName: String): CompensatingAction(CompensatingActionType.CREATE, serviceName)
class EmployeeDeletedEvent(val employeeId: Long, compensatingAction: EmployeeDeletedCompensation, serviceName: String): DomainEvent(compensatingAction, serviceName)

