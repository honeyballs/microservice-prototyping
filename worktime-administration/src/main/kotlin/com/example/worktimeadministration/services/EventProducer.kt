package com.example.worktimeadministration.services

import com.example.worktimeadministration.model.aggregates.EventAggregate


interface EventProducer {

    fun sendEventsOfAggregate(aggregate: EventAggregate)

}