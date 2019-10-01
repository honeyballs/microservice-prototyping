package com.example.projectadministration.services.kafka.employee

import com.example.projectadministration.services.EventHandler
import com.example.projectadministration.configurations.TOPIC_NAME
import com.example.projectadministration.model.employee.*
import com.example.projectadministration.model.events.DepartmentEvent
import com.example.projectadministration.model.events.EmployeeEvent
import com.example.projectadministration.model.events.PositionEvent
import com.example.projectadministration.repositories.employeeservice.DepartmentRepository
import com.example.projectadministration.repositories.employeeservice.EmployeeRepository
import com.example.projectadministration.repositories.employeeservice.PositionRepository
import com.example.projectadministration.services.kafka.KafkaEventProducer
import org.springframework.kafka.annotation.KafkaHandler
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service
import org.springframework.transaction.UnexpectedRollbackException
import java.time.LocalDateTime

@Service
@KafkaListener(groupId = "ProjectService", topics = [EMPLOYEE_TOPIC_NAME])
class EmployeeServiceEmployeeKafkaEventHandler(
        val producer: KafkaEventProducer,
        val employeeRepository: EmployeeRepository,
        val departmentRepository: DepartmentRepository,
        val positionRepository: PositionRepository): EventHandler {

    @KafkaHandler
    fun handle(event: EmployeeEvent) {
        println(event.employee.firstname)
        val employee = event.employee
        try {
            val department = departmentRepository.findByDepartmentId(employee.department.departmentId).orElseThrow()
            val position = positionRepository.findByPositionId(employee.position.positionId).orElseThrow()
            employee.department = department
            employee.position = position
            employeeRepository.save(employee)
        } catch (rollback: UnexpectedRollbackException) {
            rollback.printStackTrace()
            val comp = event.compensatingAction
            comp!!.rollbackOccurredAt(LocalDateTime.now())
            producer.sendDomainEvent(employee.employeeId, event.compensatingAction!!)
        }
    }

    @KafkaHandler(isDefault = true)
    fun defaultHandler(message: Any) {
        println("Message received: $message")
    }

}