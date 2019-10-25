package com.example.employeeadministration.services.kafka

import com.example.employeeadministration.SERVICE_NAME
import com.example.employeeadministration.model.aggregates.EMPLOYEE_AGGREGATE_NAME
import com.example.employeeadministration.model.dto.EmployeeKfk
import com.example.employeeadministration.model.aggregates.AggregateState
import com.example.employeeadministration.model.events.ResponseEvent
import com.example.employeeadministration.model.events.UpdateStateEvent
import com.example.employeeadministration.model.saga.SagaState
import com.example.employeeadministration.repositories.DepartmentRepository
import com.example.employeeadministration.repositories.EmployeeRepository
import com.example.employeeadministration.repositories.PositionRepository
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
@KafkaListener(groupId = SERVICE_NAME, topics = [EMPLOYEE_AGGREGATE_NAME])
class KafkaEmployeeEventHandler(
        val employeeRepository: EmployeeRepository,
        val departmentRepository: DepartmentRepository,
        val positionRepository: PositionRepository,
        val sagaRepository: SagaRepository,
        val sagaService: SagaService,
        val mapper: ObjectMapper,
        val eventProducer: KafkaEventProducer
) : EventHandler {

    val logger = LoggerFactory.getLogger(KafkaEmployeeEventHandler::class.java)

    @KafkaHandler
    @Transactional
    fun handleResponse(responseEvent: ResponseEvent, ack: Acknowledgment) {
        try {
            val saga = sagaRepository.getBySagaEventId(responseEvent.rootEventId).orElseThrow()
            if (getResponseEventKeyword(responseEvent.type) == "success") {
                val state = saga.receivedSuccessEvent(responseEvent.consumerName)
                if (state == SagaState.COMPLETED && !sagaService.existsAnotherSagaInRunningOrFailed(saga.id!!, saga.aggregateId)) {
                    activateEmployee(saga.aggregateId, saga.id!!)
                }
            } else if (getResponseEventKeyword(responseEvent.type) == "fail") {
                saga.receivedFailureEvent()
                rollbackEmployee(saga.aggregateId, saga.leftAggregate)
            }
            ack.acknowledge()
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    @Throws(Exception::class)
    fun activateEmployee(id: Long, sagaId: Long) {
        val emp = employeeRepository.findById(id).orElseThrow()
        emp.state = AggregateState.ACTIVE
        employeeRepository.save(emp)
        eventProducer.sendDomainEvent(id, UpdateStateEvent(getSagaCompleteType(EMPLOYEE_AGGREGATE_NAME), id, sagaId, AggregateState.ACTIVE), EMPLOYEE_AGGREGATE_NAME)
    }

    @Throws(Exception::class)
    fun rollbackEmployee(id: Long, data: String) {
        val employeeKfk = mapper.readValue<EmployeeKfk>(data)
        val emp = employeeRepository.findById(id).orElseThrow()
        emp.firstname = employeeKfk.firstname
        emp.lastname = employeeKfk.lastname
        emp.address = employeeKfk.address
        emp.bankDetails = employeeKfk.bankDetails
        emp.companyMail = employeeKfk.companyMail!!
        emp.hourlyRate = employeeKfk.hourlyRate
        emp.deleted = employeeKfk.deleted
        if (emp.department.id!! != employeeKfk.department) {
            emp.department = departmentRepository.findById(employeeKfk.id).orElseThrow()
        }
        if (emp.position.id!! != employeeKfk.position) {
            emp.position = positionRepository.findById(employeeKfk.position).orElseThrow()
        }
        employeeRepository.save(emp)
    }

    @KafkaHandler(isDefault = true)
    fun defaultHandler(message: Any) {
        println("Message received: $message")
    }

}