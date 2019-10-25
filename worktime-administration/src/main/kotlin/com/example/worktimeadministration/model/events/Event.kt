package com.example.worktimeadministration.model.events

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeName

/**
 * Defines the Event properties that every event needs which are an id and a timestamp.
 */
@JsonTypeName("event")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)
@JsonSubTypes(
        JsonSubTypes.Type(value = DomainEvent::class, name = "domainEvent"),
        JsonSubTypes.Type(value = ResponseEvent::class, name = "responseEvent"),
        JsonSubTypes.Type(value = UpdateStateEvent::class, name = "updateStateEvent")
)
interface Event {

    val originatingServiceName: String
    val id: String
    val eventCreatedAt: String
    val type: String

}
