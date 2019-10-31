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
import com.example.projectadministration.services.EventHandler
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
): EventHandler {

    val logger = LoggerFactory.getLogger(KafkaProjectEventHandler::class.java)

    @KafkaHandler
    @Transactional
    fun handleResponse(responseEvent: ResponseEvent, ack: Acknowledgment) {
        logger.info("Response Event received - From: ${responseEvent.consumerName}, type: ${responseEvent.type}")
        try {
            sagaRepository.getBySagaEventId(responseEvent.rootEventId).ifPresent {
                if (getResponseEventKeyword(responseEvent.type) == "success") {
                    val state = it.receivedSuccessEvent(responseEvent.consumerName)
                    if (state == SagaState.COMPLETED && !sagaService.existsAnotherSagaInRunningOrFailed(it.id!!, it.aggregateId)) {
                        activateProject(it.aggregateId, it.id!!)
                    }
                } else if (getResponseEventKeyword(responseEvent.type) == "fail") {
                    it.receivedFailureEvent()
                    rollbackProject(it.aggregateId, it.leftAggregate, it.rightAggregate)
                }
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
    fun rollbackProject(id: Long, data: String, failedData: String) {
        val projectKfk: ProjectKfk? =  mapper.readValue<ProjectKfk>(data)
        val project = projectRepository.findById(id).orElseThrow()
        if (projectKfk == null) {
            projectRepository.deleteById(id)
        } else {
            project.deleted = projectKfk.deleted
            project.name = projectKfk.name
            project.description = projectKfk.description
            project.projectedEndDate = projectKfk.projectedEndDate
            project.endDate = projectKfk.endDate
            project.employees = projectKfk.employees.map { employeeRepository.findByEmployeeId(id).orElseThrow() }.toMutableSet()
            projectRepository.save(project)
        }
        // Check the employee service employee kafka handler for rollback event
    }

    @KafkaHandler(isDefault = true)
    fun defaultHandler(message: Any) {
        println("Message received: $message")
    }

}