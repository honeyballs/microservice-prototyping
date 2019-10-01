package com.example.projectadministration.model.events

import com.example.projectadministration.SERVICE_NAME
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeName
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

const val DATE_TIME_PATTERN = "dd.MM.yyyy HH:mm:ss:SSS"

/**
 * Represents an event occurring in the domain.
 * It implements the base [Event] interface and contains a compensating event in case a participant is unable to process it.
 */
@JsonTypeName("domainEvent")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)
@JsonSubTypes(
        JsonSubTypes.Type(value = EmployeeEvent::class, name = "employeeEvent"),
        JsonSubTypes.Type(value = DepartmentEvent::class, name = "departmentEvent"),
        JsonSubTypes.Type(value = PositionEvent::class, name = "positionEvent")
)
open class DomainEvent(
        override val id: String,
        override val eventCreatedAt: String,
        compensatingAction: CompensatingAction,
        override val type: EventType,
        override val originatingServiceName: String = SERVICE_NAME) : Event {

    open var compensatingAction: CompensatingAction? = null
        set(value) {
            if (value != null) {
                value.originalEventId = id
            }
            field = value
        }

    init {
        this.compensatingAction = compensatingAction
    }

    constructor(compensatingAction: CompensatingAction, type: EventType): this(UUID.randomUUID().toString(), LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)), compensatingAction, type)

}