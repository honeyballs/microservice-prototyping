package com.example.employeeadministration.model.events

/**
 * Defines base functionality of an aggregate which emits events.
 */
open class EventAggregate {

    private val events = ArrayList<DomainEvent>()

    fun registerEvent(event: DomainEvent) {
        events.add(event)
    }

    fun events(): List<DomainEvent> {
        return events
    }

    fun clearEvents() {
        events.clear()
    }

}