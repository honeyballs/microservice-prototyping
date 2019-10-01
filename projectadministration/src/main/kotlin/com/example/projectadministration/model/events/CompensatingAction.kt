package com.example.projectadministration.model.events

import com.example.projectadministration.SERVICE_NAME
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeName
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * Defines possible compensation actions.
 */
enum class CompensatingActionType {
    CREATE, UPDATE, DELETE
}

/**
 * Baseclass for compensation events. Since the compensation is in itself an event it implements the [Event] interface.
 * It contains the id of the event that needs to be compensated and what type of compensation has to be done.
 * Compensations extending this class provide the necessary data to execute the compensation.
 */
@JsonTypeName("compensatingAction")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)
@JsonSubTypes(
        JsonSubTypes.Type(value = EmployeeCompensation::class, name = "employeeCompensation"),
        JsonSubTypes.Type(value = DepartmentCompensation::class, name = "departmentCompensation"),
        JsonSubTypes.Type(value = PositionCompensation::class, name = "positionCompensation")
)
open class CompensatingAction(
        override val id: String,
        override var eventCreatedAt: String = "",
        var originalEventId: String? = "",
        override val type: EventType,
        override val originatingServiceName: String = SERVICE_NAME) : Event {

    constructor(type: EventType): this(UUID.randomUUID().toString(), "", "", type)

    fun rollbackOccurredAt(time: LocalDateTime) {
        eventCreatedAt = time.format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN))
    }

}