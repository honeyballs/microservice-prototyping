package com.example.employeeadministration.services

import com.example.employeeadministration.model.aggregates.EventAggregate

interface EventProducer {

    fun sendEventsOfAggregate(aggregate: EventAggregate)

}