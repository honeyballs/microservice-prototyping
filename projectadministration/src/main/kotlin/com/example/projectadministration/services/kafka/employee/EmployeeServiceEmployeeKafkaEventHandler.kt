package com.example.projectadministration.services.kafka.employee

import com.example.projectadministration.services.EventHandler
import com.example.projectadministration.configurations.TOPIC_NAME
import com.example.projectadministration.model.employee.*
import com.example.projectadministration.model.events.DepartmentEvent
import com.example.projectadministration.model.events.EmployeeEvent
import com.example.projectadministration.model.events.EventType
import com.example.projectadministration.model.events.PositionEvent
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
@KafkaListener(groupId = "ProjectService", topics = [EMPLOYEE_TOPIC_NAME])
class EmployeeServiceEmployeeKafkaEventHandler(
        val producer: KafkaEventProducer,
        val employeeRepository: EmployeeRepository,
        val departmentRepository: DepartmentRepository,
        val positionRepository: PositionRepository): EventHandler {

    @KafkaHandler
    fun handle(event: EmployeeEvent, ack: Acknowledgment) {
        println(event.employee.firstname)
        val eventEmployee = event.employee
        try {

            when (event.type) {
                EventType.CREATE -> createEmployee(eventEmployee)
                EventType.UPDATE -> updateEmployee(eventEmployee)
                EventType.DELETE -> deleteEmployee(eventEmployee)
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
    fun createEmployee(eventEmployee: Employee) {
        val department = departmentRepository.findByDepartmentId(eventEmployee.department.departmentId).orElseThrow()
        val position = positionRepository.findByPositionId(eventEmployee.position.positionId).orElseThrow()
        eventEmployee.department = department
        eventEmployee.position = position
        employeeRepository.save(eventEmployee)
    }

    @Throws(RollbackException::class, Exception::class)
    fun updateEmployee(eventEmployee: Employee) {
        // If we would not load beforehand a new db row would be created because dbId is only set in this service
        val emp = employeeRepository.findByEmployeeId(eventEmployee.employeeId).orElseThrow()
        if (emp.department.departmentId != eventEmployee.department.departmentId) {
            eventEmployee.department = departmentRepository.findByDepartmentId(eventEmployee.department.departmentId).orElseThrow()
        }
        if (emp.position.positionId != eventEmployee.position.positionId) {
            eventEmployee.position = positionRepository.findByPositionId(eventEmployee.position.positionId).orElseThrow()
        }
        eventEmployee.dbId = emp.dbId
        employeeRepository.save(eventEmployee)
    }

    @Throws(RollbackException::class, Exception::class)
    fun deleteEmployee(eventEmployee: Employee) {
        val emp = employeeRepository.findByEmployeeId(eventEmployee.employeeId).orElseThrow()
        emp.deleted = true
        employeeRepository.save(emp)
    }

    fun handleCompensation(event: EmployeeEvent) {
        val comp = event.compensatingAction
        comp!!.rollbackOccurredAt(LocalDateTime.now())
        producer.sendDomainEvent(event.employee.employeeId, event.compensatingAction!!)
    }

    @KafkaHandler(isDefault = true)
    fun defaultHandler(message: Any) {
        println("Message received: $message")
    }

}