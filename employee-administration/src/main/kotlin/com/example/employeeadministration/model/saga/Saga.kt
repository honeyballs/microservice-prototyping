package com.example.employeeadministration.model.saga

import com.example.employeeadministration.model.events.ResponseEvent
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import javax.persistence.*

/**
 * A Saga controls aggregate changes which have to be consistent.
 * Services interested in an event of an aggregate have to respond. The saga keeps track of received responses.
 * The required [ResponseEvent]s for the event the saga is keeping track of can be configured in the saga.properties file.
 * Not all events require a saga. When no receivers are configured no saga is created.
 * Aggregate services are responsible for creating a saga, for example the [com.example.employeeadministration.services.DepartmentServiceImpl].
 */
@Entity
class Saga(
        @Id @GeneratedValue(strategy = GenerationType.AUTO) var id: Long?,
        @Embedded val triggerEvent: TriggerEvent,
        val aggregateId: Long,
        var requiredSuccessEvents: String,
        var receivedSuccessEvents: String = "",
        var sagaState: SagaState = SagaState.RUNNING
) {

    fun receivedSuccessEvent(consumerName: String): SagaState {
        val required = convertStringToList(requiredSuccessEvents)
        val received = convertStringToList(receivedSuccessEvents)
        if (!received.contains(consumerName)) {
            received.add(consumerName)
        }
        if (received.size == required.size) {
            sagaState = SagaState.COMPLETED
        }
        println("received size: ${received.size}, required size: ${required.size}")
        receivedSuccessEvents = convertListToString(received)
        return sagaState
    }

    fun receivedFailureEvent() {
        this.sagaState = SagaState.FAILED
    }

}

