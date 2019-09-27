package com.example.employeeadministration.model.events

import com.example.employeeadministration.SERVICE_NAME
import com.fasterxml.jackson.annotation.*
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

const val DATE_TIME_PATTERN = "dd.MM.yyyy HH:mm:ss:SSS"

/**
 * Represents an event occurring in the domain.
 * It implements the base [Event] interface and contains a compensating event in case a participant is unable to process it.
 */
open class DomainEvent(
        override val id: String,
        override val eventCreatedAt: String,
        compensatingAction: CompensatingAction,
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

    constructor(compensatingAction: CompensatingAction): this(UUID.randomUUID().toString(), LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)), compensatingAction)

}