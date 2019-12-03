package com.example.projectadministration.services

import com.example.projectadministration.model.events.DomainEvent
import com.example.projectadministration.model.saga.Saga
import com.example.projectadministration.model.saga.SagaState
import com.example.projectadministration.model.saga.TriggerEvent
import com.example.projectadministration.repositories.SagaRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Service

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