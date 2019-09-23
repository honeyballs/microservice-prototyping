package com.example.employeeadministration.services

interface EventProducingPersistenceService<AggregateType> {

    fun persistWithEvents(aggregate: AggregateType): AggregateType

}