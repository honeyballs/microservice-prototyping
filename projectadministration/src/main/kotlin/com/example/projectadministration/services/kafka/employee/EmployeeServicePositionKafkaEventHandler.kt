package com.example.projectadministration.services.kafka.employee

import com.example.projectadministration.services.EventHandler
import com.example.projectadministration.configurations.TOPIC_NAME
import com.example.projectadministration.model.employee.DEPARTMENT_TOPIC_NAME
import com.example.projectadministration.model.employee.Department
import com.example.projectadministration.model.employee.POSITION_TOPIC_NAME
import com.example.projectadministration.model.employee.Position
import com.example.projectadministration.model.events.DepartmentEvent
import com.example.projectadministration.model.events.EventType
import com.example.projectadministration.model.events.PositionEvent
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
@KafkaListener(groupId = "ProjectService", topics = [POSITION_TOPIC_NAME])
class EmployeeServicePositionKafkaEventHandler(
        val producer: KafkaEventProducer,
        val positionRepository: PositionRepository): EventHandler {

    val logger = LoggerFactory.getLogger(EmployeeServicePositionKafkaEventHandler::class.java)

    @KafkaHandler
    @Transactional
    fun handle(event: PositionEvent, ack: Acknowledgment) {
        logger.info("Position Event received. Type: ${event.type}, Id: ${event.position.positionId}")
        val eventPosition = event.position
        try {

            when (event.type) {
                EventType.CREATE -> createPosition(eventPosition)
                EventType.UPDATE -> updatePosition(eventPosition)
                EventType.DELETE -> deletePosition(eventPosition)
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
    fun createPosition(eventPosition: Position) {
        positionRepository.save(eventPosition)
    }

    @Throws(RollbackException::class, Exception::class)
    fun updatePosition(eventPosition: Position) {
        // If we would not load beforehand a new db row would be created because dbId is only set in this service
        val pos = positionRepository.findByPositionId(eventPosition.positionId).orElseThrow()
        eventPosition.dbId = pos.dbId
        positionRepository.save(eventPosition)
    }

    @Throws(RollbackException::class, Exception::class)
    fun deletePosition(eventPosition: Position) {
        val pos = positionRepository.findByPositionId(eventPosition.positionId).orElseThrow()
        pos.deleted = true
        positionRepository.save(pos)
    }

    fun handleCompensation(event: PositionEvent) {
        val comp = event.compensatingAction
        comp!!.rollbackOccurredAt(LocalDateTime.now())
        producer.sendDomainEvent(event.position.positionId, event.compensatingAction!!)
    }


    @KafkaHandler(isDefault = true)
    fun defaultHandler(message: Any) {
        println("Message received: $message")
    }

}