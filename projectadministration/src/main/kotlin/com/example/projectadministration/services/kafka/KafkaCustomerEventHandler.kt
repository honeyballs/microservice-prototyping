package com.example.projectadministration.services.kafka

import com.example.projectadministration.SERVICE_NAME
import com.example.projectadministration.model.aggregates.AggregateState
import com.example.projectadministration.model.aggregates.CUSTOMER_AGGREGATE_NAME
import com.example.projectadministration.model.dto.CustomerKfk
import com.example.projectadministration.model.events.ResponseEvent
import com.example.projectadministration.model.events.UpdateStateEvent
import com.example.projectadministration.model.saga.SagaState
import com.example.projectadministration.repositories.CustomerRepository
import com.example.projectadministration.repositories.SagaRepository
import com.example.projectadministration.services.EventHandler
import com.example.projectadministration.services.SagaService
import com.example.projectadministration.services.getResponseEventKeyword
import com.example.projectadministration.services.getSagaCompleteType
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaHandler
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@KafkaListener(groupId = SERVICE_NAME, topics = [CUSTOMER_AGGREGATE_NAME])
class KafkaCustomerEventHandler(
        val customerRepository: CustomerRepository,
        val sagaRepository: SagaRepository,
        val sagaService: SagaService,
        val mapper: ObjectMapper,
        val eventProducer: KafkaEventProducer
): EventHandler {

    val logger = LoggerFactory.getLogger(KafkaCustomerEventHandler::class.java)


    @KafkaHandler
    @Transactional
    fun handleResponse(responseEvent: ResponseEvent, ack: Acknowledgment) {
        try {
            sagaRepository.getBySagaEventId(responseEvent.rootEventId).ifPresent {
                if (getResponseEventKeyword(responseEvent.type) == "success") {
                    val state = it.receivedSuccessEvent(responseEvent.consumerName)
                    if (state == SagaState.COMPLETED && !sagaService.existsAnotherSagaInRunningOrFailed(it.id!!, it.aggregateId)) {
                        activateCustomer(it.aggregateId, it.id!!)
                    }
                } else if (getResponseEventKeyword(responseEvent.type) == "fail") {
                    it.receivedFailureEvent()
                    rollbackCustomer(it.aggregateId, it.leftAggregate, it.rightAggregate)
                }
            }
            ack.acknowledge()
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    @Throws(Exception::class)
    fun activateCustomer(id: Long, sagaId: Long) {
        val customer = customerRepository.findById(id).orElseThrow()
        customer.state = AggregateState.ACTIVE
        customerRepository.save(customer)
        eventProducer.sendDomainEvent(id, UpdateStateEvent(getSagaCompleteType(CUSTOMER_AGGREGATE_NAME), id, sagaId, AggregateState.ACTIVE), CUSTOMER_AGGREGATE_NAME)
    }

    @Throws(Exception::class)
    fun rollbackCustomer(id: Long, data: String, failedData: String) {
        val customerKfk: CustomerKfk? = mapper.readValue<CustomerKfk>(data)
        val customer = customerRepository.findById(id).orElseThrow()
        if (customerKfk == null) {
            customerRepository.deleteById(id)
        } else {
            customer.deleted = customerKfk.deleted
            customer.address = customerKfk.address
            customer.contact = customerKfk.contact
            customer.customerName = customerKfk.customerName
            customerRepository.save(customer)
        }
        // Check the employee service employee kafka handler for rollback event
    }

    @KafkaHandler(isDefault = true)
    fun defaultHandler(message: Any) {
        println("Message received: $message")
    }

}