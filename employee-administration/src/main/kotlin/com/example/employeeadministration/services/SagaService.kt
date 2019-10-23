package com.example.employeeadministration.services

import com.example.employeeadministration.model.events.DomainEvent
import com.example.employeeadministration.model.saga.Saga
import com.example.employeeadministration.repositories.SagaRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Service

@Service
class SagaService(val sagaRepository: SagaRepository, val mapper: ObjectMapper) {

    fun createSagaOfEvent(event: DomainEvent, aggregateId: Long, requiredEvents: String) {
        val saga = Saga(
                null,
                event.id,
                event.type,
                aggregateId,
                mapper.writeValueAsString(event.from),
                mapper.writeValueAsString(event.to),
                requiredEvents
        )
        sagaRepository.save(saga)
    }

}