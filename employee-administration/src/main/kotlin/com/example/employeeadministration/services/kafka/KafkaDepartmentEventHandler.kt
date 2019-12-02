package com.example.employeeadministration.services.kafka

import com.example.employeeadministration.SERVICE_NAME
import com.example.employeeadministration.model.aggregates.DEPARTMENT_AGGREGATE_NAME
import com.example.employeeadministration.model.dto.DepartmentKfk
import com.example.employeeadministration.model.aggregates.AggregateState
import com.example.employeeadministration.model.dto.PositionKfk
import com.example.employeeadministration.model.events.*
import com.example.employeeadministration.model.saga.SagaState
import com.example.employeeadministration.repositories.DepartmentRepository
import com.example.employeeadministration.repositories.SagaRepository
import com.example.employeeadministration.services.*
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
            sagaRepository.getByTriggerEventEventId(responseEvent.rootEventId).ifPresent {
                if (getResponseEventKeyword(responseEvent.type) == "success") {
                    val state = it.receivedSuccessEvent(responseEvent.consumerName)
                    if (state == SagaState.COMPLETED && !sagaService.existsAnotherSagaInRunningOrFailed(it.id!!, it.aggregateId)) {
                        activateDepartment(it.aggregateId, it.id!!)
                    }
                } else if (getResponseEventKeyword(responseEvent.type) == "fail") {
                    it.receivedFailureEvent()
                    compensateDepartment(it.aggregateId, it.triggerEvent.leftAggregate, it.triggerEvent.rightAggregate)
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
    fun compensateDepartment(id: Long, data: String, failedData: String) {
        val departmentKfk: DepartmentKfk? = mapper.readValue<DepartmentKfk>(data)
        val failedDepartmentKfk = mapper.readValue<PositionKfk>(failedData)
        val dep = departmentRepository.findById(id).orElseThrow()
        if (departmentKfk == null) {
            departmentRepository.deleteById(id)
        } else {
            dep.deleted = departmentKfk.deleted
            dep.name = departmentKfk.name
            dep.state = AggregateState.ACTIVE
            departmentRepository.save(dep)
        }
        // Build the compensation event
        val eventType = getEventTypeFromProperties(dep.aggregateName, "compensation")
        val successResponseType = getResponseEventType(eventType, false)
        val failureResponseType = getResponseEventType(eventType, true)
        val event = DomainEvent(eventType, departmentKfk, failedDepartmentKfk, successResponseType, failureResponseType)
        eventProducer.sendDomainEvent(dep.id!!, event, dep.aggregateName)
    }


    @KafkaHandler(isDefault = true)
    fun defaultHandler(message: Any) {
        println("Message received: $message")
    }

}