package com.example.worktimeadministration.model.aggregates

import com.example.worktimeadministration.model.dto.BaseKfkDto
import com.example.worktimeadministration.model.events.DomainEvent
import com.example.worktimeadministration.model.events.ResponseEvent
import com.example.worktimeadministration.services.getEventTypeFromProperties
import com.example.worktimeadministration.services.getResponseEventType
import javax.persistence.MappedSuperclass
import javax.persistence.Transient

/**
 * Defines base functionality of an aggregate which emits events.
 * Aggregates have a state which controls whether changes can be made on it.
 */
@MappedSuperclass
abstract class EventAggregate {

    @Transient
    open lateinit var aggregateName: String

    // Since events need a key property corresponding to the id of an aggregate a pair is used to store them.
    @Transient
    private var events: Pair<Long, MutableList<DomainEvent>>? = null

    var state: AggregateState = AggregateState.PENDING

    fun registerEvent(aggregateId: Long, action: String, from: BaseKfkDto?, vararg additionalResponseEvents: ResponseEvent) {
        val eventType = getEventTypeFromProperties(aggregateName, action)
        // Always add a success and fail
        val successResponse = ResponseEvent(getResponseEventType(eventType, false))
        val failureResponse = ResponseEvent(getResponseEventType(eventType, true))
        val event = DomainEvent(eventType, from, mapAggregateToKafkaDto(), successResponse, failureResponse)
        additionalResponseEvents.forEach { event.addResponseEvent(it) }
        if (events == null) {
            events = Pair(aggregateId, ArrayList())
        }
        events!!.second.add(event)
    }

    fun events(): Pair<Long, MutableList<DomainEvent>>? {
        return events
    }

    fun clearEvents() {
        events = null
    }

    abstract fun mapAggregateToKafkaDto(): BaseKfkDto

}

enum class AggregateState {
    PENDING, ACTIVE, FAILED
}