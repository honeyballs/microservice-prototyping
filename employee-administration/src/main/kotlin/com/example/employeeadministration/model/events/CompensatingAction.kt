package com.example.employeeadministration.model.events

import com.example.employeeadministration.SERVICE_NAME
import com.example.employeeadministration.model.Position
import com.fasterxml.jackson.annotation.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


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