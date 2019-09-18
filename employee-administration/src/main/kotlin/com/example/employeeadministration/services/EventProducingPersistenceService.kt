package com.example.employeeadministration.services

import com.example.employeeadministration.kafka.EventProducer
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.transaction.UnexpectedRollbackException

interface EventProducingPersistenceService<AggregateType> {

    fun persistWithEvents(aggregate: AggregateType): AggregateType

}