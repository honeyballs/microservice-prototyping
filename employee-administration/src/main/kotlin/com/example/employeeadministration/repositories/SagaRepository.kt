package com.example.employeeadministration.repositories

import com.example.employeeadministration.model.saga.Saga
import com.example.employeeadministration.model.saga.SagaState
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface SagaRepository: JpaRepository<Saga, Long> {

    fun getBySagaEventId(eventId: String): Optional<Saga>
    fun getByAggregateIdAndSagaState(aggregateId: Long, state: SagaState): List<Saga>

}