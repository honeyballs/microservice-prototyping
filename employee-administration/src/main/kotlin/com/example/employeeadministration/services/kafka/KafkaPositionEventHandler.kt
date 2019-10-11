package com.example.employeeadministration.services.kafka

import com.example.employeeadministration.model.POSITION_AGGREGATE_NAME
import com.example.employeeadministration.model.PositionKfk
import com.example.employeeadministration.model.events.AggregateState
import com.example.employeeadministration.model.events.ResponseEvent
import com.example.employeeadministration.model.events.UpdateStateEvent
import com.example.employeeadministration.model.saga.SagaState
import com.example.employeeadministration.repositories.PositionRepository
import com.example.employeeadministration.repositories.SagaRepository
import com.example.employeeadministration.services.EventHandler
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
@KafkaListener(groupId = "EmployeeService", topics = [POSITION_AGGREGATE_NAME])
class KafkaPositionEventHandler(
        val positionRepository: PositionRepository,
        val sagaRepository: SagaRepository,
        val mapper: ObjectMapper,
        val eventProducer: KafkaEventProducer
): EventHandler {

    val logger = LoggerFactory.getLogger(KafkaPositionEventHandler::class.java)

    @KafkaHandler
    @Transactional
    fun handleResponse(responseEvent: ResponseEvent, ack: Acknowledgment) {
        try {
            val saga = sagaRepository.getBySagaEventId(responseEvent.rootEventId).orElseThrow()
            if (getResponseEventKeyword(responseEvent.type) == "success") {
                val state = saga.receivedSuccessEvent(responseEvent.consumerName)
                if (state == SagaState.COMPLETED) {
                    activatePosition(saga.aggregateId, saga.id!!)
                }
            } else if (getResponseEventKeyword(responseEvent.type) == "fail") {
                saga.receivedFailureEvent()
                rollbackPosition(saga.aggregateId, saga.leftAggregate)
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
    fun rollbackPosition(id: Long, data: String) {
        val positionKfk = mapper.readValue<PositionKfk>(data)
        val pos = positionRepository.findById(id).orElseThrow()
        pos.deleted = positionKfk.deleted
        pos.title = positionKfk.title
        pos.minHourlyWage = positionKfk.minHourlyWage
        pos.maxHourlyWage = positionKfk.maxHourlyWage
        positionRepository.save(pos)
    }

    @KafkaHandler(isDefault = true)
    fun defaultHandler(message: Any) {
        println("Message received: $message")
    }

}