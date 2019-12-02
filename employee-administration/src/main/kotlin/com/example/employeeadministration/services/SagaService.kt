package com.example.employeeadministration.services

import com.example.employeeadministration.model.events.DomainEvent
import com.example.employeeadministration.model.saga.Saga
import com.example.employeeadministration.model.saga.SagaState
import com.example.employeeadministration.model.saga.TriggerEvent
import com.example.employeeadministration.repositories.SagaRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Service

@Service
class SagaService(val sagaRepository: SagaRepository, val mapper: ObjectMapper) {

    fun createSagaOfEvent(event: DomainEvent, aggregateId: Long, requiredEvents: String) {
        val saga = Saga(
                null,
                mapDomainEventToTriggerEvent(event),
                aggregateId,
                requiredEvents
        )
        sagaRepository.save(saga)
    }

    fun mapDomainEventToTriggerEvent(event: DomainEvent): TriggerEvent {
        return TriggerEvent(
                event.id,
                event.eventCreatedAt,
                event.type,
                mapper.writeValueAsString(event.from),
                mapper.writeValueAsString(event.to),
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