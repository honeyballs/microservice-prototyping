

/**
 * Collection of relevant domain events and compensations for departments and job positions.
 */

class DepartmentCreatedCompensation(val departmentId: Long, serviceName: String): CompensatingAction(CompensatingActionType.DELETE, serviceName)
class DepartmentCreatedEvent constructor(val departmentId: Long, val name: String, compensatingAction: DepartmentCreatedCompensation, serviceName: String): DomainEvent(compensatingAction, serviceName)

class DepartmentChangedNameCompensation(val departmentId: Long, val oldName: String, serviceName: String): CompensatingAction(CompensatingActionType.UPDATE, serviceName)
class DepartmentChangedNameEvent(val departmentId: Long, val name: String, compensatingAction: DepartmentChangedNameCompensation, serviceName: String): DomainEvent(compensatingAction, serviceName)

class DepartmentDeletedCompensation(val departmentId: Long, serviceName: String): CompensatingAction(CompensatingActionType.CREATE, serviceName)
class DepartmentDeletedEvent(val departmentId: Long, compensatingAction: DepartmentDeletedCompensation, serviceName: String): DomainEvent(compensatingAction, serviceName)

class PositionCreatedCompensation(val positionId: Long, serviceName: String): CompensatingAction(CompensatingActionType.DELETE, serviceName)
class PositionCreatedEvent(val positionId: Long, val title: String, compensatingAction: PositionCreatedCompensation, serviceName: String): DomainEvent(compensatingAction, serviceName)

class PositionChangedTitleCompensation(val positionId: Long, val title: String, serviceName: String): CompensatingAction(CompensatingActionType.UPDATE, serviceName)
class PositionChangedTitleEvent(val positionId: Long, val title: String, compensatingAction: PositionChangedTitleCompensation, serviceName: String): DomainEvent(compensatingAction, serviceName)

class PositionDeletedCompensation(val positionId: Long, serviceName: String): CompensatingAction(CompensatingActionType.CREATE, serviceName)
class PositionDeletedEvent(val positionId: Long, compensatingAction: PositionDeletedCompensation, serviceName: String): DomainEvent(compensatingAction, serviceName)