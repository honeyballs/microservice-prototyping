package com.example.employeeadministration.services.kafka

import com.example.employeeadministration.model.DepartmentKfk
import com.example.employeeadministration.model.events.DomainEvent
import com.example.employeeadministration.model.saga.Saga
import com.example.employeeadministration.repositories.SagaRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Service

@Service
class SagaService(val sagaRepository: SagaRepository, val mapper: ObjectMapper) {

    fun <DataType> createSagaOfEvent(event: DomainEvent<DataType>, aggregateId: Long, requiredEvents: String) {
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