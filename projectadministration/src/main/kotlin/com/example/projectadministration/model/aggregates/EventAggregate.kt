package com.example.projectadministration.model.aggregates

import com.example.projectadministration.model.events.DomainEvent
import com.example.projectadministration.model.events.ResponseEvent
import com.example.projectadministration.services.getEventTypeFromProperties
import com.example.projectadministration.services.getResponseEventType
import javax.persistence.MappedSuperclass
import javax.persistence.Transient

/**
 * Defines base functionality of an aggregate which emits events.
 * Aggregates have a state which controls whether changes can be made on it.
 */
@MappedSuperclass
abstract class EventAggregate<DataType> {

    @Transient
    lateinit var aggregateName: String

    // Since events need a key property corresponding to the id of an aggregate a pair is used to store them.
    @Transient
    private var events: Pair<Long, MutableList<DomainEvent<DataType>>>? = null

    var state: AggregateState = AggregateState.PENDING

    fun registerEvent(aggregateId: Long, action: String, from: DataType?, vararg additionalResponseEvents: ResponseEvent) {
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

    fun events(): Pair<Long, MutableList<DomainEvent<DataType>>>? {
        return events
    }

    fun clearEvents() {
        events = null
    }

    abstract fun mapAggregateToKafkaDto(): DataType

}

enum class AggregateState {
    PENDING, ACTIVE, FAILED
}