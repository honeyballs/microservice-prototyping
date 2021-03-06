package com.example.employeeadministration.services

import com.example.employeeadministration.model.events.DomainEvent
import com.example.employeeadministration.model.saga.Saga
import com.example.employeeadministration.model.saga.SagaState
import com.example.employeeadministration.model.saga.TriggerEvent
import com.example.employeeadministration.repositories.SagaRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Service
class SagaService(val sagaRepository: SagaRepository, val mapper: ObjectMapper) {

    fun createSagaOfEvent(emittedEvent: DomainEvent, aggregateId: Long, requiredEvents: String, triggerEvent: DomainEvent?) {
        val saga = Saga(
                null,
                aggregateId,
                mapper.writeValueAsString(emittedEvent.from),
                mapper.writeValueAsString(emittedEvent.to),
                emittedEvent.id,
                mapDomainEventToTriggerEvent(triggerEvent),
                requiredEvents
        )
        sagaRepository.save(saga)
        println("saga created with emittedEvent event id: ${emittedEvent.id}")
    }

    fun mapDomainEventToTriggerEvent(event: DomainEvent?): TriggerEvent? {
        if (event == null) {
            return null
        }
        return TriggerEvent(
                event.id,
                event.eventCreatedAt,
                event.type,
                event.successEventType,
                event.failureEventType,
                event.additionalResponseEventTypes,
                event.originatingServiceName
        )
    }

    fun existsAnotherSagaInRunningOrFailed(id: Long, aggregateId: Long): Boolean {
        return sagaRepository.existsAllByIdNotAndAggregateIdAndSagaStateIn(id, aggregateId, listOf(SagaState.RUNNING, SagaState.FAILED))
    }

}