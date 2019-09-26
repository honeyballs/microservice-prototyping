package com.example.projectadministration.model.events

import com.example.projectadministration.model.events.DomainEvent

/**
 * Defines base functionality of an aggregate which emits events.
 */
open class EventAggregate {

    // Since events need a key property corresponding to the id of an aggregate a pair is used to store them.
    private var events: Pair<Long, MutableList<DomainEvent>>? = null

    fun registerEvent(aggregateId: Long, event: DomainEvent) {
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

}