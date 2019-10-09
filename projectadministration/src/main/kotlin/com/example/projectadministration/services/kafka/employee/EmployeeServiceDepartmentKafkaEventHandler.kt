package com.example.projectadministration.services.kafka.employee

import com.example.projectadministration.services.EventHandler
import com.example.projectadministration.configurations.TOPIC_NAME
import com.example.projectadministration.model.employee.*
import com.example.projectadministration.model.events.DepartmentEvent
import com.example.projectadministration.model.events.EventType
import com.example.projectadministration.repositories.employeeservice.DepartmentRepository
import com.example.projectadministration.repositories.employeeservice.EmployeeRepository
import com.example.projectadministration.repositories.employeeservice.PositionRepository
import com.example.projectadministration.services.kafka.KafkaEventProducer
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaHandler
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Service
import org.springframework.transaction.UnexpectedRollbackException
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import javax.persistence.RollbackException

@Service
@KafkaListener(groupId = "ProjectService", topics = [DEPARTMENT_TOPIC_NAME])
class EmployeeServiceDepartmentKafkaEventHandler(
        val producer: KafkaEventProducer,
        val departmentRepository: DepartmentRepository): EventHandler {

    val logger = LoggerFactory.getLogger(EmployeeServiceDepartmentKafkaEventHandler::class.java)

    @KafkaHandler
    @Transactional
    fun handle(event: DepartmentEvent, ack: Acknowledgment) {
        logger.info("Department Event received. Type: ${event.type}, Id: ${event.department.id}")
        val eventDepartment = event.department
        try {

            when (event.type) {
                EventType.CREATE -> createDepartment(eventDepartment)
                EventType.UPDATE -> updateDepartment(eventDepartment)
                EventType.DELETE -> deleteDepartment(eventDepartment)
            }

        } catch (rollback: UnexpectedRollbackException) {
            rollback.printStackTrace()
            handleCompensation(event)
        } catch (exception: Exception) {
            exception.printStackTrace()
            handleCompensation(event)
        } finally {
            ack.acknowledge()
        }
    }

    @Throws(RollbackException::class, Exception::class)
    fun createDepartment(eventDepartment: DepartmentKfk) {
        val dep = Department(null, eventDepartment.id, eventDepartment.name, eventDepartment.deleted)
        departmentRepository.save(dep)
    }

    @Throws(RollbackException::class, Exception::class)
    fun updateDepartment(eventDepartment: DepartmentKfk) {
        // If we would not load beforehand a new db row would be created because dbId is only set in this service
        val dep = departmentRepository.findByDepartmentId(eventDepartment.id).orElseThrow()
        dep.name = eventDepartment.name
        departmentRepository.save(dep)
    }

    @Throws(RollbackException::class, Exception::class)
    fun deleteDepartment(eventDepartment: DepartmentKfk) {
        val dep = departmentRepository.findByDepartmentId(eventDepartment.id).orElseThrow()
        dep.deleted = true
        departmentRepository.save(dep)
    }

    fun handleCompensation(event: DepartmentEvent) {
        val comp = event.compensatingAction
        comp!!.rollbackOccurredAt(LocalDateTime.now())
        producer.sendDomainEvent(event.department.id, event.compensatingAction!!, DEPARTMENT_TOPIC_NAME)
    }

    @KafkaHandler(isDefault = true)
    fun defaultHandler(message: Any) {
        println("Message received: $message")
    }

}