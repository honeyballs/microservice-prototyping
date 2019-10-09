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
@KafkaListener(groupId = "ProjectService", topics = [EMPLOYEE_TOPIC_NAME])
class EmployeeServiceEmployeeKafkaEventHandler(
        val producer: KafkaEventProducer,
        val employeeRepository: EmployeeRepository,
        val departmentRepository: DepartmentRepository,
        val positionRepository: PositionRepository): EventHandler {

    val logger = LoggerFactory.getLogger(EmployeeServiceEmployeeKafkaEventHandler::class.java)

    @KafkaHandler
    @Transactional
    fun handle(event: EmployeeEvent, ack: Acknowledgment) {
        logger.info("Employee Event received. Type: ${event.type}, Id: ${event.employee.id}")
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
    fun createEmployee(eventEmployee: EmployeeKfk) {
        val department = departmentRepository.findByDepartmentId(eventEmployee.department).orElseThrow()
        val position = positionRepository.findByPositionId(eventEmployee.position).orElseThrow()
        val emp = Employee(null, eventEmployee.id, eventEmployee.firstname, eventEmployee.lastname, department, position, eventEmployee.companyMail)
        employeeRepository.save(emp)
    }

    @Throws(RollbackException::class, Exception::class)
    fun updateEmployee(eventEmployee: EmployeeKfk) {
        // If we would not load beforehand a new db row would be created because dbId is only set in this service
        val emp = employeeRepository.findByEmployeeId(eventEmployee.id).orElseThrow()
        if (emp.department.departmentId != eventEmployee.department) {
            emp.department = departmentRepository.findByDepartmentId(eventEmployee.department).orElseThrow()
        }
        if (emp.position.positionId != eventEmployee.position) {
            emp.position = positionRepository.findByPositionId(eventEmployee.position).orElseThrow()
        }
        emp.firstname = eventEmployee.firstname
        emp.lastname = eventEmployee.lastname
        emp.companyMail = eventEmployee.companyMail
        employeeRepository.save(emp)
    }

    @Throws(RollbackException::class, Exception::class)
    fun deleteEmployee(eventEmployee: EmployeeKfk) {
        val emp = employeeRepository.findByEmployeeId(eventEmployee.id).orElseThrow()
        emp.deleted = true
        employeeRepository.save(emp)
    }

    fun handleCompensation(event: EmployeeEvent) {
        val comp = event.compensatingAction
        comp!!.rollbackOccurredAt(LocalDateTime.now())
        producer.sendDomainEvent(event.employee.id, event.compensatingAction!!, EMPLOYEE_TOPIC_NAME)
    }

    @KafkaHandler(isDefault = true)
    fun defaultHandler(message: Any) {
        println("Message received: $message")
    }

}