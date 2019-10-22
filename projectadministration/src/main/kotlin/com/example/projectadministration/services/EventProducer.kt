package com.example.projectadministration.services

import com.example.projectadministration.model.aggregates.EventAggregate

interface EventProducer {

    fun <KafkaDtoType> sendEventsOfAggregate(aggregate: EventAggregate<KafkaDtoType>)

}