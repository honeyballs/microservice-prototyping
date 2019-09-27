package com.example.projectadministration.services.kafka

import com.example.projectadministration.services.EventHandler
import com.example.projectadministration.configurations.TOPIC_NAME
import com.example.projectadministration.model.events.EmployeeCreatedEvent
import com.example.projectadministration.model.events.PositionCreatedEvent
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
        val employeeRepository: EmployeeRepository,
        val positionRepository: PositionRepository): EventHandler {

    override fun handle(event: PositionCreatedEvent) {
        val position = event.position
        try {
            positionRepository.save(position)
        } catch (rollback: UnexpectedRollbackException) {
            rollback.printStackTrace()
            val comp = event.compensatingAction
            comp!!.rollbackOccurredAt(LocalDateTime.now())
            producer.sendDomainEvent(event.position.id, event.compensatingAction!!)
        }
    }

    override fun handle(event: EmployeeCreatedEvent) {
        val employee = event.employee
        try {
            employeeRepository.save(employee)
        } catch (rollback: UnexpectedRollbackException) {
            rollback.printStackTrace()
            producer.sendDomainEvent(event.employee.id, event.compensatingAction!!)
        }
    }


    @KafkaHandler(isDefault = true)
    fun defaultHandler(message: Any) {
        println("Message received: $message")
    }

}