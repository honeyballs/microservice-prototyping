package com.example.employeeadministration.services

import com.example.employeeadministration.model.aggregates.EventAggregate

interface EventProducer {

    fun <KafkaDtoType> sendEventsOfAggregate(aggregate: EventAggregate<KafkaDtoType>)

}