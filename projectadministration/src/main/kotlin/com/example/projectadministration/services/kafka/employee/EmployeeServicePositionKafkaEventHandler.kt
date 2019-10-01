package com.example.projectadministration.services.kafka.employee

import com.example.projectadministration.services.EventHandler
import com.example.projectadministration.configurations.TOPIC_NAME
import com.example.projectadministration.model.employee.DEPARTMENT_TOPIC_NAME
import com.example.projectadministration.model.employee.Department
import com.example.projectadministration.model.employee.POSITION_TOPIC_NAME
import com.example.projectadministration.model.employee.Position
import com.example.projectadministration.model.events.DepartmentEvent
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
@KafkaListener(groupId = "ProjectService", topics = [POSITION_TOPIC_NAME])
class EmployeeServicePositionKafkaEventHandler(
        val producer: KafkaEventProducer,
        val positionRepository: PositionRepository): EventHandler {

    @KafkaHandler
    fun handle(event: PositionEvent) {
        println(event.position.title)
        val position = event.position
        try {
            positionRepository.save(position)
        } catch (rollback: UnexpectedRollbackException) {
            rollback.printStackTrace()
            val comp = event.compensatingAction
            comp!!.rollbackOccurredAt(LocalDateTime.now())
            producer.sendDomainEvent(position.positionId, event.compensatingAction!!)
        }
    }

    @KafkaHandler(isDefault = true)
    fun defaultHandler(message: Any) {
        println("Message received: $message")
    }

}