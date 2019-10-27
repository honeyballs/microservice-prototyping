package com.example.employeeadministration.model.events

import com.example.employeeadministration.SERVICE_NAME
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeName
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * When a service emits an event the event needs to contain a success and a fail response for the receiving service to respond with.
 * This does not mean that every recipient has to answer. The recipients which must answer are configured in the saga.properties file.
 * Each event type configured in the event-types.properties needs a .success and a .fail type to construct ResponseEvents from.
 */
@JsonTypeName("responseEvent")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)
class ResponseEvent(
        override val originatingServiceName: String,
        override val id: String,
        override val eventCreatedAt: String,
        override val type: String
): Event {

    var consumerName = ""
    var consumerMessage: String? = null
    lateinit var rootEventId: String

    constructor(type: String): this(SERVICE_NAME, UUID.randomUUID().toString(), LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)), type)

}