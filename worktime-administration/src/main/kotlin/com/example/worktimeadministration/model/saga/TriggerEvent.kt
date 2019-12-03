package com.example.worktimeadministration.model.saga

import javax.persistence.Embeddable
import javax.persistence.Lob

@Embeddable
class TriggerEvent(
        val eventId: String,
        val eventCreatedAt: String,
        val type: String,
        val successEventType: String,
        val failureEventType: String,
        val additionalResponseEventTypesString: String,
        val originatingServiceName: String
)  {

    constructor(
            eventId: String,
            eventCreatedAt: String,
            type: String,
            successEventType: String,
            failureEventType: String,
            additionalResponseEventTypes: Set<String>,
            originatingServiceName: String
    ): this(eventId, eventCreatedAt, type, successEventType, failureEventType, convertListToString(additionalResponseEventTypes.toList()), originatingServiceName)


    fun getResponseTypesAsSet(): Set<String> {
        return convertStringToList(this.additionalResponseEventTypesString).toSet()
    }

}