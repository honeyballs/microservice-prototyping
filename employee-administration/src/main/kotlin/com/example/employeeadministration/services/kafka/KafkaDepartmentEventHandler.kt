package com.example.employeeadministration.services.kafka

import com.example.employeeadministration.SERVICE_NAME
import com.example.employeeadministration.model.aggregates.DEPARTMENT_AGGREGATE_NAME
import com.example.employeeadministration.model.dto.DepartmentKfk
import com.example.employeeadministration.model.aggregates.AggregateState
import com.example.employeeadministration.model.events.ResponseEvent
import com.example.employeeadministration.model.events.UpdateStateEvent
import com.example.employeeadministration.model.saga.SagaState
import com.example.employeeadministration.repositories.DepartmentRepository
import com.example.employeeadministration.repositories.SagaRepository
import com.example.employeeadministration.services.EventHandler
import com.example.employeeadministration.services.SagaService
import com.example.employeeadministration.services.getResponseEventKeyword
import com.example.employeeadministration.services.getSagaCompleteType
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaHandler
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@KafkaListener(groupId = SERVICE_NAME, topics = [DEPARTMENT_AGGREGATE_NAME])
class KafkaDepartmentEventHandler(
        val departmentRepository: DepartmentRepository,
        val sagaRepository: SagaRepository,
        val sagaService: SagaService,
        val mapper: ObjectMapper,
        val eventProducer: KafkaEventProducer
): EventHandler {

    val logger = LoggerFactory.getLogger(KafkaDepartmentEventHandler::class.java)

    @KafkaHandler
    @Transactional
    fun handleResponse(responseEvent: ResponseEvent, ack: Acknowledgment) {
        try {
            sagaRepository.getBySagaEventId(responseEvent.rootEventId).ifPresent {
                if (getResponseEventKeyword(responseEvent.type) == "success") {
                    val state = it.receivedSuccessEvent(responseEvent.consumerName)
                    if (state == SagaState.COMPLETED && !sagaService.existsAnotherSagaInRunningOrFailed(it.id!!, it.aggregateId)) {
                        activateDepartment(it.aggregateId, it.id!!)
                    }
                } else if (getResponseEventKeyword(responseEvent.type) == "fail") {
                    it.receivedFailureEvent()
                    rollbackDepartment(it.aggregateId, it.leftAggregate, it.rightAggregate)
                }
            }
            ack.acknowledge()
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    @Throws(Exception::class)
    fun activateDepartment(id: Long, sagaId: Long) {
        val dep = departmentRepository.findById(id).orElseThrow()
        dep.state = AggregateState.ACTIVE
        departmentRepository.save(dep)
        eventProducer.sendDomainEvent(id, UpdateStateEvent(getSagaCompleteType(DEPARTMENT_AGGREGATE_NAME), id, sagaId, AggregateState.ACTIVE), DEPARTMENT_AGGREGATE_NAME)
    }

    @Throws(Exception::class)
    fun rollbackDepartment(id: Long, data: String, failedData: String) {
        val departmentKfk: DepartmentKfk? = mapper.readValue<DepartmentKfk>(data)
        val dep = departmentRepository.findById(id).orElseThrow()
        if (departmentKfk == null) {
            departmentRepository.deleteById(id)
        } else {
            dep.deleted = departmentKfk.deleted
            dep.name = departmentKfk.name
            departmentRepository.save(dep)
        }
        // Check the employee kafka handler for rollback event
    }


    @KafkaHandler(isDefault = true)
    fun defaultHandler(message: Any) {
        println("Message received: $message")
    }

}