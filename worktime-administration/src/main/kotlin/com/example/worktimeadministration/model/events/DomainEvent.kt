package com.example.worktimeadministration.model.events

import com.example.worktimeadministration.SERVICE_NAME
import com.example.worktimeadministration.model.dto.BaseKfkDto
import com.fasterxml.jackson.annotation.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

const val DATE_TIME_PATTERN = "dd.MM.yyyy HH:mm:ss:SSS"

/**
 * Represents an event occurring in the domain.
 * It implements the base [Event] interface and contains [ResponseEvent]s another service can answer with.
 * Per default each DomainEvent contains a success and failure response. Further responses can be registered.
 *
 * @see [ResponseEvent] Check the Response Event for configuration and additional information
 */
@JsonTypeName("domainEvent")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)
open class DomainEvent(
        override val id: String,
        override val eventCreatedAt: String,
        override val type: String,
        val from: BaseKfkDto?,
        val to: BaseKfkDto,
        val successEvent: ResponseEvent,
        val failureEvent: ResponseEvent,
        override val originatingServiceName: String = SERVICE_NAME
) : Event {

    init {
        successEvent.rootEventId = id
        failureEvent.rootEventId = id
    }

    var additionalResponseEvents = setOf<ResponseEvent>()

    constructor(type: String, from: BaseKfkDto?, to: BaseKfkDto, successEvent: ResponseEvent, failureEvent: ResponseEvent): this(UUID.randomUUID().toString(), LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)), type, from, to, successEvent, failureEvent)

    fun addResponseEvent(response: ResponseEvent) {
        additionalResponseEvents = additionalResponseEvents.plus(response)
    }

}