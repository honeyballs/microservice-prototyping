package com.example.employeeadministration.model.aggregates

import com.example.employeeadministration.model.events.DomainEvent
import com.example.employeeadministration.services.getEventTypeFromProperties
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

    fun registerEvent(aggregateId: Long, action: String, from: DataType?) {
        val event = DomainEvent(getEventTypeFromProperties(aggregateName, action), from, mapAggregateToKafkaDto())
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