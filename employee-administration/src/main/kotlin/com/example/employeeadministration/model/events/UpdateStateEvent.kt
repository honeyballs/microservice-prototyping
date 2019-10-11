package com.example.employeeadministration.model.events

import com.example.employeeadministration.SERVICE_NAME
import com.example.employeeadministration.model.aggregates.AggregateState
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeName
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * After a Saga has finished the service informs other services to update the state of received aggregates.
 */
@JsonTypeName("updateStateEvent")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)
class UpdateStateEvent(
        override val originatingServiceName: String,
        override val id: String,
        override val eventCreatedAt: String,
        override val type: String,
        val aggregateId: Long,
        val sagaId: Long,
        val state: AggregateState
) : Event {

    constructor(type: String, aggregateId: Long, sagaId: Long, state: AggregateState):
            this(SERVICE_NAME, UUID.randomUUID().toString(), LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)), type, aggregateId, sagaId, state)

}