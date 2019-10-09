package com.example.projectadministration.services

import com.example.projectadministration.model.events.EventAggregate

interface EventProducer {

    fun <KafkaDtoType> sendEventsOfAggregate(aggregate: EventAggregate<KafkaDtoType>)

}