package com.example.projectadministration.services.kafka

import com.example.projectadministration.services.EventHandler
import com.example.projectadministration.configurations.TOPIC_NAME
import com.example.projectadministration.model.employee.Department
import com.example.projectadministration.model.employee.Employee
import com.example.projectadministration.model.employee.Position
import com.example.projectadministration.model.events.DepartmentCreatedEvent
import com.example.projectadministration.model.events.EmployeeCreatedEvent
import com.example.projectadministration.model.events.PositionCreatedEvent
import com.example.projectadministration.repositories.employeeservice.DepartmentRepository
import com.example.projectadministration.repositories.employeeservice.EmployeeRepository
import com.example.projectadministration.repositories.employeeservice.PositionRepository
import com.example.projectadministration.services.EventProducingPersistenceService
import org.springframework.kafka.annotation.KafkaHandler
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service
import org.springframework.transaction.UnexpectedRollbackException
import java.time.LocalDateTime

@Service
@KafkaListener(groupId = "ProjectService", topics = [TOPIC_NAME])
class EmployeeServiceKafkaEventHandler(
        val producer: KafkaEventProducer,
        val departmentRepository: DepartmentRepository,
        val employeeRepository: EmployeeRepository,
        val positionRepository: PositionRepository): EventHandler {

    @KafkaHandler
    override fun handle(event: DepartmentCreatedEvent) {
        val department = Department(null, event.departmentId, event.name)
        try {
            departmentRepository.save(department)
        } catch (rollback: UnexpectedRollbackException) {
            rollback.printStackTrace()
            val comp = event.compensatingAction
            comp!!.rollbackOccurredAt(LocalDateTime.now())
            producer.sendDomainEvent(event.departmentId, event.compensatingAction!!)
        }
    }

    @KafkaHandler
    override fun handle(event: PositionCreatedEvent) {
        val position = Position(null, event.positionId, event.title)
        try {
            positionRepository.save(position)
        } catch (rollback: UnexpectedRollbackException) {
            rollback.printStackTrace()
            val comp = event.compensatingAction
            comp!!.rollbackOccurredAt(LocalDateTime.now())
            producer.sendDomainEvent(event.positionId, event.compensatingAction!!)
        }
    }

    @KafkaHandler
    override fun handle(event: EmployeeCreatedEvent) {
//        val employee = Employee(null, event.employeeId, event.firstname, event.lastname, event.departmentId, event.positionId)
//        try {
//            employeeRepository.save(employee)
//        } catch (rollback: UnexpectedRollbackException) {
//            rollback.printStackTrace()
//            producer.sendDomainEvent(event.employee.id, event.compensatingAction!!)
//        }
    }


    @KafkaHandler(isDefault = true)
    fun defaultHandler(message: Any) {
        println("Message received: $message")
    }

}