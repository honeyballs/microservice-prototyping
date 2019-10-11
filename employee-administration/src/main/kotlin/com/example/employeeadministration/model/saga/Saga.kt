package com.example.employeeadministration.model.saga

import com.example.employeeadministration.model.events.ResponseEvent
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import javax.persistence.*

@Entity
class Saga(
        @Id @GeneratedValue(strategy = GenerationType.AUTO) var id: Long?,
        val sagaEventId: String,
        val eventType: String,
        val aggregateId: Long,
        @Lob val leftAggregate: String,
        @Lob val rightAggregate: String,
        var requiredSuccessEvents: String,
        var receivedSuccessEvents: String = "",
        var sagaState: SagaState = SagaState.RUNNING
) {

    fun receivedSuccessEvent(consumerName: String): SagaState {
        val required = convertToList(requiredSuccessEvents)
        val received = convertToList(receivedSuccessEvents)
        if (!received.contains(consumerName)) {
            received.add(consumerName)
        }
        if (received.size == required.size) {
            sagaState = SagaState.COMPLETED
        }
        println("received size: ${received.size}, required size: ${required.size}")
        receivedSuccessEvents = convertToString(received)
        return sagaState
    }

    fun receivedFailureEvent() {
        this.sagaState = SagaState.FAILED
    }

    private fun convertToList(str: String): MutableList<String> {
        return str.split(",").filter { it != "" }.toMutableList()
    }
    
    private fun convertToString(strings: List<String>): String {
        return strings.reduce { acc, s ->  "$acc,$s"}.removePrefix(",")
    }

}

