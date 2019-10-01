package com.example.projectadministration.model.events

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
        JsonSubTypes.Type(value = CompensatingAction::class, name = "compensatingAction")
)
interface Event {

    val originatingServiceName: String
    val id: String
    val eventCreatedAt: String
    val type: EventType

}

/**
 * Defines possible compensation actions.
 */
enum class EventType {
    CREATE, UPDATE, DELETE
}