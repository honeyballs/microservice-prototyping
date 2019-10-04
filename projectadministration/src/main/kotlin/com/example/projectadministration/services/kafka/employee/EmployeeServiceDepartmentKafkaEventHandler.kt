package com.example.projectadministration.services.kafka.employee

import com.example.projectadministration.services.EventHandler
import com.example.projectadministration.configurations.TOPIC_NAME
import com.example.projectadministration.model.employee.DEPARTMENT_TOPIC_NAME
import com.example.projectadministration.model.employee.Department
import com.example.projectadministration.model.employee.Position
import com.example.projectadministration.model.events.DepartmentEvent
import com.example.projectadministration.model.events.EventType
import com.example.projectadministration.repositories.employeeservice.DepartmentRepository
import com.example.projectadministration.repositories.employeeservice.EmployeeRepository
import com.example.projectadministration.repositories.employeeservice.PositionRepository
import com.example.projectadministration.services.kafka.KafkaEventProducer
import org.springframework.kafka.annotation.KafkaHandler
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Service
import org.springframework.transaction.UnexpectedRollbackException
import java.time.LocalDateTime
import javax.persistence.RollbackException

@Service
@KafkaListener(groupId = "ProjectService", topics = [DEPARTMENT_TOPIC_NAME])
class EmployeeServiceDepartmentKafkaEventHandler(
        val producer: KafkaEventProducer,
        val departmentRepository: DepartmentRepository): EventHandler {


    @KafkaHandler
    fun handle(event: DepartmentEvent, ack: Acknowledgment) {
        println(event.department.name)
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
    fun createDepartment(eventDepartment: Department) {
        departmentRepository.save(eventDepartment)
    }

    @Throws(RollbackException::class, Exception::class)
    fun updateDepartment(eventDepartment: Department) {
        // If we would not load beforehand a new db row would be created because dbId is only set in this service
        val dep = departmentRepository.findByDepartmentId(eventDepartment.departmentId).orElseThrow()
        eventDepartment.dbId = dep.dbId
        departmentRepository.save(eventDepartment)
    }

    @Throws(RollbackException::class, Exception::class)
    fun deleteDepartment(eventDepartment: Department) {
        val dep = departmentRepository.findByDepartmentId(eventDepartment.departmentId).orElseThrow()
        dep.deleted = true
        departmentRepository.save(dep)
    }

    fun handleCompensation(event: DepartmentEvent) {
        val comp = event.compensatingAction
        comp!!.rollbackOccurredAt(LocalDateTime.now())
        producer.sendDomainEvent(event.department.departmentId, event.compensatingAction!!)
    }

    @KafkaHandler(isDefault = true)
    fun defaultHandler(message: Any) {
        println("Message received: $message")
    }

}