package com.example.worktimeadministration.services

interface EventProducingPersistenceService<AggregateType> {

    fun persistWithEvents(aggregate: AggregateType): AggregateType

}