package com.example.projectadministration.services.kafka

import com.example.projectadministration.SERVICE_NAME
import com.example.projectadministration.model.aggregates.AggregateState
import com.example.projectadministration.model.aggregates.CUSTOMER_AGGREGATE_NAME
import com.example.projectadministration.model.aggregates.PROJECT_AGGREGATE_NAME
import com.example.projectadministration.model.dto.CustomerKfk
import com.example.projectadministration.model.dto.ProjectKfk
import com.example.projectadministration.model.events.ResponseEvent
import com.example.projectadministration.model.events.UpdateStateEvent
import com.example.projectadministration.model.saga.SagaState
import com.example.projectadministration.repositories.CustomerRepository
import com.example.projectadministration.repositories.ProjectRepository
import com.example.projectadministration.repositories.SagaRepository
import com.example.projectadministration.repositories.employee.EmployeeRepository
import com.example.projectadministration.services.SagaService
import com.example.projectadministration.services.getResponseEventKeyword
import com.example.projectadministration.services.getSagaCompleteType
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaHandler
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@KafkaListener(groupId = SERVICE_NAME, topics = [PROJECT_AGGREGATE_NAME])
class KafkaProjectEventHandler(
        val projectRepository: ProjectRepository,
        val employeeRepository: EmployeeRepository,
        val sagaRepository: SagaRepository,
        val sagaService: SagaService,
        val mapper: ObjectMapper,
        val eventProducer: KafkaEventProducer
) {

    val logger = LoggerFactory.getLogger(KafkaProjectEventHandler::class.java)


    @KafkaHandler
    @Transactional
    fun handleResponse(responseEvent: ResponseEvent, ack: Acknowledgment) {
        try {
            val saga = sagaRepository.getBySagaEventId(responseEvent.rootEventId).orElseThrow()
            if (getResponseEventKeyword(responseEvent.type) == "success") {
                val state = saga.receivedSuccessEvent(responseEvent.consumerName)
                if (state == SagaState.COMPLETED && !sagaService.existsAnotherSagaInRunningOrFailed(saga.id!!, saga.aggregateId)) {
                    activateProject(saga.aggregateId, saga.id!!)
                }
            } else if (getResponseEventKeyword(responseEvent.type) == "fail") {
                saga.receivedFailureEvent()
                rollbackProject(saga.aggregateId, saga.leftAggregate)
            }
            ack.acknowledge()
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    @Throws(Exception::class)
    fun activateProject(id: Long, sagaId: Long) {
        val project = projectRepository.findById(id).orElseThrow()
        project.state = AggregateState.ACTIVE
        projectRepository.save(project)
        eventProducer.sendDomainEvent(id, UpdateStateEvent(getSagaCompleteType(PROJECT_AGGREGATE_NAME), id, sagaId, AggregateState.ACTIVE), PROJECT_AGGREGATE_NAME)
    }

    @Throws(Exception::class)
    fun rollbackProject(id: Long, data: String) {
        val projectKfk = mapper.readValue<ProjectKfk>(data)
        val project = projectRepository.findById(id).orElseThrow()
        project.deleted = projectKfk.deleted
        project.name = projectKfk.name
        project.description = projectKfk.description
        project.projectedEndDate = projectKfk.projectedEndDate
        project.endDate = projectKfk.projectedEndDate
        project.employees = projectKfk.employees.map { employeeRepository.findByEmployeeId(id).orElseThrow() }.toSet()
        projectRepository.save(project)
    }

    @KafkaHandler(isDefault = true)
    fun defaultHandler(message: Any) {
        println("Message received: $message")
    }

}