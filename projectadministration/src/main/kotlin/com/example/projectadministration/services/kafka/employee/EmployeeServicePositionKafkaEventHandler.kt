package com.example.projectadministration.services.kafka.employee

import com.example.projectadministration.SERVICE_NAME
import com.example.projectadministration.model.aggregates.AggregateState
import com.example.projectadministration.model.aggregates.employee.DEPARTMENT_AGGREGATE_NAME
import com.example.projectadministration.model.aggregates.employee.Department
import com.example.projectadministration.model.aggregates.employee.POSITION_AGGREGATE_NAME
import com.example.projectadministration.model.aggregates.employee.Position
import com.example.projectadministration.model.dto.employee.DepartmentKfk
import com.example.projectadministration.services.EventHandler
import com.example.projectadministration.model.dto.employee.PositionKfk
import com.example.projectadministration.model.events.DomainEvent
import com.example.projectadministration.model.events.UpdateStateEvent
import com.example.projectadministration.repositories.employee.PositionRepository
import com.example.projectadministration.services.getActionOfConsumedEvent
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
@KafkaListener(groupId = SERVICE_NAME, topics = [POSITION_AGGREGATE_NAME])
class EmployeeServicePositionKafkaEventHandler(
        val producer: KafkaEventProducer,
        val positionRepository: PositionRepository): EventHandler {

    val logger = LoggerFactory.getLogger(EmployeeServicePositionKafkaEventHandler::class.java)

    @KafkaHandler
    @Transactional
    fun handle(event: DomainEvent, ack: Acknowledgment) {
        logger.info("Position Event received. Type: ${event.type}, Id: ${event.to.id}")
        val action = getActionOfConsumedEvent(event.type)
        val eventPos = event.to
        try {

            when(action) {
                "created" -> createPosition(eventPos as PositionKfk)
                "updated" -> updatePosition(eventPos as PositionKfk)
                "deleted" -> deletePosition(eventPos as PositionKfk)
                "compensation" -> compensatePosition(eventPos as PositionKfk, event.from as? PositionKfk)
            }

            val success = event.successEvent
            success.consumerName = SERVICE_NAME
            producer.sendDomainEvent(eventPos.id, success, POSITION_AGGREGATE_NAME)

        } catch (rollback: UnexpectedRollbackException) {
            rollback.printStackTrace()
            val failure = event.failureEvent
            failure.consumerName = SERVICE_NAME
            producer.sendDomainEvent(eventPos.id, failure, POSITION_AGGREGATE_NAME)
        } catch (exception: Exception) {
            exception.printStackTrace()
            val failure = event.failureEvent
            failure.consumerName = SERVICE_NAME
            producer.sendDomainEvent(eventPos.id, failure, POSITION_AGGREGATE_NAME)
        } finally {
            ack.acknowledge()
        }
    }

    @KafkaHandler
    @Transactional
    fun handle(event: UpdateStateEvent, ack: Acknowledgment) {
        logger.info("Position Saga State Event received")
        try {
            changeAggregateState(event.aggregateId, event.state)
        } catch (rollback: UnexpectedRollbackException) {
            rollback.printStackTrace()
        } catch (exception: Exception) {
            exception.printStackTrace()
        } finally {
            ack.acknowledge()
        }
    }

    @Throws(RollbackException::class, Exception::class)
    fun createPosition(eventPos: PositionKfk) {
        val position = Position(null, eventPos.id, eventPos.title, eventPos.deleted, eventPos.state)
        positionRepository.save(position)
    }

    @Throws(RollbackException::class, Exception::class)
    fun updatePosition(eventPos: PositionKfk) {
        val position = positionRepository.findByPositionId(eventPos.id).orElseThrow()
        position.title = eventPos.title
        position.state = eventPos.state
        positionRepository.save(position)
    }

    @Throws(RollbackException::class, Exception::class)
    fun deletePosition(eventPos: PositionKfk) {
        val position = positionRepository.findByPositionId(eventPos.id).orElseThrow()
        position.deleted = true
        position.state = eventPos.state
        positionRepository.save(position)
    }

    @Throws(RollbackException::class, Exception::class)
    fun compensatePosition(failedValue: PositionKfk, compensationValue: PositionKfk?) {
        positionRepository.findByPositionId(failedValue.id).ifPresent {
            if (compensationValue == null) {
                positionRepository.deleteById(it.dbId!!)
            } else {
                it.title = compensationValue.title
                it.deleted = compensationValue.deleted
                it.state = AggregateState.ACTIVE
                positionRepository.save(it)
            }
        }
    }

    @Throws(RollbackException::class, Exception::class)
    fun changeAggregateState(id: Long, state: AggregateState) {
        val position = positionRepository.findByPositionId(id).orElseThrow()
        position.state = state
        positionRepository.save(position)
    }


    @KafkaHandler(isDefault = true)
    fun defaultHandler(message: Any) {
        println("Message received: $message")
    }

}