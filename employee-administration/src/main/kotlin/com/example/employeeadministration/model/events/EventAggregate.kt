package com.example.employeeadministration.model.events

import com.example.employeeadministration.services.getEventTypeFromProperties
import javax.xml.crypto.Data

/**
 * Defines base functionality of an aggregate which emits events.
 */
abstract class EventAggregate<DataType> {

    lateinit var TOPIC_NAME: String
    lateinit var aggregate: String

    // Since events need a key property corresponding to the id of an aggregate a pair is used to store them.
    private var events: Pair<Long, MutableList<DomainEvent<DataType>>>? = null

    fun registerEvent(aggregateId: Long, action: String, from: DataType?) {
        val event = DomainEvent(getEventTypeFromProperties(aggregate, action), from, mapAggregateToKafkaDto())
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