package com.example.projectadministration.services

import com.example.projectadministration.model.aggregates.EventAggregate

interface EventProducer {

    fun sendEventsOfAggregate(aggregate: EventAggregate)

}