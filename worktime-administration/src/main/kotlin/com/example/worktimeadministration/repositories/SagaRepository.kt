package com.example.worktimeadministration.repositories

import com.example.worktimeadministration.model.saga.Saga
import com.example.worktimeadministration.model.saga.SagaState
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface SagaRepository: JpaRepository<Saga, Long> {

    fun getByEmittedEventId(eventId: String): Optional<Saga>
    fun getByAggregateIdAndSagaState(aggregateId: Long, state: SagaState): List<Saga>
    fun existsAllByIdNotAndAggregateIdAndSagaStateIn(id: Long, aggregateId: Long, states: List<SagaState>): Boolean

}