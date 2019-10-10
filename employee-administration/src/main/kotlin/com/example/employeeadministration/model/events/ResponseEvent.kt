package com.example.employeeadministration.model.events

import com.example.employeeadministration.SERVICE_NAME
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeName
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@JsonTypeName("responseEvent")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)
class ResponseEvent(
        override val originatingServiceName: String,
        override val id: String,
        override val eventCreatedAt: String,
        override val type: String,
        val rootEventId: String
): Event {

    constructor(type: String, rootEventId: String): this(SERVICE_NAME, UUID.randomUUID().toString(), LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)), type, rootEventId)

}