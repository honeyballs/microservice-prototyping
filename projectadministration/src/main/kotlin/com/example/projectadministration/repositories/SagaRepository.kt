package com.example.projectadministration.repositories

import com.example.projectadministration.model.saga.Saga
import com.example.projectadministration.model.saga.SagaState
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface SagaRepository: JpaRepository<Saga, Long> {

    fun getBySagaEventId(eventId: String): Optional<Saga>
    fun getByAggregateIdAndSagaState(aggregateId: Long, state: SagaState): List<Saga>
    fun existsAllByIdNotAndAggregateIdAndSagaStateIn(id: Long, aggregateId: Long, states: List<SagaState>): Boolean

}