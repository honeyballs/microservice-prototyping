package com.example.employeeadministration.services.kafka

import com.example.employeeadministration.SERVICE_NAME
import com.example.employeeadministration.model.DEPARTMENT_AGGREGATE_NAME
import com.example.employeeadministration.model.DepartmentKfk
import com.example.employeeadministration.model.events.AggregateState
import com.example.employeeadministration.model.events.ResponseEvent
import com.example.employeeadministration.model.events.UpdateStateEvent
import com.example.employeeadministration.model.saga.SagaState
import com.example.employeeadministration.repositories.DepartmentRepository
import com.example.employeeadministration.repositories.SagaRepository
import com.example.employeeadministration.services.EventHandler
import com.example.employeeadministration.services.getResponseEventKeyword
import com.example.employeeadministration.services.getResponseEventType
import com.example.employeeadministration.services.getSagaCompleteType
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaHandler
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Service
import org.springframework.transaction.UnexpectedRollbackException
import org.springframework.transaction.annotation.Transactional

@Service
@KafkaListener(groupId = SERVICE_NAME, topics = [DEPARTMENT_AGGREGATE_NAME])
class KafkaDepartmentEventHandler(
        val departmentRepository: DepartmentRepository,
        val sagaRepository: SagaRepository,
        val mapper: ObjectMapper,
        val eventProducer: KafkaEventProducer
): EventHandler {

    val logger = LoggerFactory.getLogger(KafkaDepartmentEventHandler::class.java)

    @KafkaHandler
    @Transactional
    fun handleResponse(responseEvent: ResponseEvent, ack: Acknowledgment) {
        try {
            val saga = sagaRepository.getBySagaEventId(responseEvent.rootEventId).orElseThrow()
            if (getResponseEventKeyword(responseEvent.type) == "success") {
                val state = saga.receivedSuccessEvent(responseEvent.consumerName)
                if (state == SagaState.COMPLETED) {
                    activateDepartment(saga.aggregateId, saga.id!!)
                }
            } else if (getResponseEventKeyword(responseEvent.type) == "fail") {
                saga.receivedFailureEvent()
                rollbackDepartment(saga.aggregateId, saga.leftAggregate)
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
    fun rollbackDepartment(id: Long, data: String) {
        val departmentKfk = mapper.readValue<DepartmentKfk>(data)
        val dep = departmentRepository.findById(id).orElseThrow()
        dep.deleted = departmentKfk.deleted
        dep.name = departmentKfk.name
        departmentRepository.save(dep)
    }


    @KafkaHandler(isDefault = true)
    fun defaultHandler(message: Any) {
        println("Message received: $message")
    }

}