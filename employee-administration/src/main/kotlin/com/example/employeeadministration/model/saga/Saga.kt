package com.example.employeeadministration.model.saga

import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import javax.persistence.*

@Entity
class Saga(
        @Id @GeneratedValue(strategy = GenerationType.AUTO) var id: Long?,
        val sagaEventId: String,
        val eventType: String,
        val aggregateId: Long,
        val from: String,
        val to: String,
        var requiredSuccessEvents: Int,
        var countDown: Int = -1,
        var sagaState: SagaState = SagaState.RUNNING
) {

    init {
        if (countDown == -1) {
            countDown = requiredSuccessEvents
        }
    }

    fun receivedSuccessEvent() {
        if (countDown == 0 || countDown-- == 0) {
            this.sagaState = SagaState.COMPLETED
        }
    }

    fun receiveFailureEvent() {
        this.sagaState = SagaState.FAILED
    }

}

