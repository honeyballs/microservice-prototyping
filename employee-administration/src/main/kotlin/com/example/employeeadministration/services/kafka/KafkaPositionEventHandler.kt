package com.example.employeeadministration.services.kafka

import com.example.employeeadministration.SERVICE_NAME
import com.example.employeeadministration.model.aggregates.POSITION_AGGREGATE_NAME
import com.example.employeeadministration.model.dto.PositionKfk
import com.example.employeeadministration.model.aggregates.AggregateState
import com.example.employeeadministration.model.events.ResponseEvent
import com.example.employeeadministration.model.events.UpdateStateEvent
import com.example.employeeadministration.model.saga.SagaState
import com.example.employeeadministration.repositories.PositionRepository
import com.example.employeeadministration.repositories.SagaRepository
import com.example.employeeadministration.services.EventHandler
import com.example.employeeadministration.services.SagaService
import com.example.employeeadministration.services.getResponseEventKeyword
import com.example.employeeadministration.services.getSagaCompleteType
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaHandler
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@KafkaListener(groupId = SERVICE_NAME, topics = [POSITION_AGGREGATE_NAME])
class KafkaPositionEventHandler(
        val positionRepository: PositionRepository,
        val sagaRepository: SagaRepository,
        val sagaService: SagaService,
        val mapper: ObjectMapper,
        val eventProducer: KafkaEventProducer
): EventHandler {

    val logger = LoggerFactory.getLogger(KafkaPositionEventHandler::class.java)

    @KafkaHandler
    @Transactional
    fun handleResponse(responseEvent: ResponseEvent, ack: Acknowledgment) {
        try {
            sagaRepository.getBySagaEventId(responseEvent.rootEventId).ifPresent {
                if (getResponseEventKeyword(responseEvent.type) == "success") {
                    val state = it.receivedSuccessEvent(responseEvent.consumerName)
                    if (state == SagaState.COMPLETED && !sagaService.existsAnotherSagaInRunningOrFailed(it.id!!, it.aggregateId)) {
                        activatePosition(it.aggregateId, it.id!!)
                    }
                } else if (getResponseEventKeyword(responseEvent.type) == "fail") {
                    it.receivedFailureEvent()
                    rollbackPosition(it.aggregateId, it.leftAggregate, it.rightAggregate)
                }
            }
            ack.acknowledge()
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    @Throws(Exception::class)
    fun activatePosition(id: Long, sagaId: Long) {
        val pos = positionRepository.findById(id).orElseThrow()
        pos.state = AggregateState.ACTIVE
        positionRepository.save(pos)
        eventProducer.sendDomainEvent(id, UpdateStateEvent(getSagaCompleteType(POSITION_AGGREGATE_NAME), id, sagaId, AggregateState.ACTIVE), POSITION_AGGREGATE_NAME)
    }

    @Throws(Exception::class)
    fun rollbackPosition(id: Long, data: String, failedData: String) {
        val positionKfk: PositionKfk? = mapper.readValue<PositionKfk>(data)
        val pos = positionRepository.findById(id).orElseThrow()
        if (positionKfk == null) {
            positionRepository.deleteById(id)
        } else {
            pos.deleted = positionKfk.deleted
            pos.title = positionKfk.title
            pos.minHourlyWage = positionKfk.minHourlyWage
            pos.maxHourlyWage = positionKfk.maxHourlyWage
            positionRepository.save(pos)
        }
        // Check the employee kafka handler for rollback event
    }

    @KafkaHandler(isDefault = true)
    fun defaultHandler(message: Any) {
        println("Message received: $message")
    }

}