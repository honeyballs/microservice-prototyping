package com.example.worktimeadministration.model.saga

import javax.persistence.Embeddable
import javax.persistence.Lob

@Embeddable
class TriggerEvent(
        val eventId: String,
        val eventCreatedAt: String,
        val type: String,
        @Lob val leftAggregate: String,
        @Lob val rightAggregate: String,
        val successEventType: String,
        val failureEventType: String,
        val additionalResponseEventTypesString: String,
        val originatingServiceName: String
)  {

    constructor(
            eventId: String,
            eventCreatedAt: String,
            type: String,
            from: String,
            to: String,
            successEventType: String,
            failureEventType: String,
            additionalResponseEventTypes: Set<String>,
            originatingServiceName: String
    ): this(eventId, eventCreatedAt, type, from, to, successEventType, failureEventType, convertListToString(additionalResponseEventTypes.toList()), originatingServiceName)


    fun getResponseTypesAsSet(): Set<String> {
        return convertStringToList(this.additionalResponseEventTypesString).toSet()
    }

}