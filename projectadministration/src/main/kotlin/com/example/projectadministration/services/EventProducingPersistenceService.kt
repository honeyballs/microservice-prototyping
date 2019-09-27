package com.example.projectadministration.services

interface EventProducingPersistenceService<AggregateType> {

    fun persistWithEvents(aggregate: AggregateType): AggregateType

}