package com.example.employeeadministration.services

import com.example.employeeadministration.model.events.DomainEvent
import com.example.employeeadministration.model.events.EventAggregate

interface EventProducer {

    fun sendEventsOfAggregate(aggregate: EventAggregate)

}