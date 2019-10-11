package com.example.employeeadministration.services.kafka

import com.example.employeeadministration.model.events.DomainEvent
import com.example.employeeadministration.model.events.Event
import com.example.employeeadministration.model.events.EventAggregate
import com.example.employeeadministration.model.saga.Saga
import com.example.employeeadministration.repositories.SagaRepository
import com.example.employeeadministration.services.EventProducer
import com.example.employeeadministration.services.getRequiredSuccessEvents
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.clients.producer.ProducerRecord
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

/**
 * Service which sends Domain Events to Kafka.
 */
@Service
class KafkaEventProducer(val kafkaTemplate: KafkaTemplate<Long, Event>, val mapper: ObjectMapper, val sagaRepository: SagaRepository) : EventProducer {

    val logger = LoggerFactory.getLogger("KafkaEventProducer")

    /**
     * Send a domain Event.
     *
     * @param key This key should be the id of the aggregate the event is concerning.
     * This key guarantees that all events of an aggregate are kept within the same partition in the correct order in Kafka.
     */
    fun sendDomainEvent(key: Long, event: Event, topic: String) {
        val record = ProducerRecord<Long, Event>(topic, key, event)
        val result = kafkaTemplate.send(record)
    }

    /**
     * Function used to send all events which occurred on an aggregate.
     * Clears the events after sending.
     * TODO: Wait for success before clearing?
     */
    override fun <KafkaDtoType> sendEventsOfAggregate(aggregate: EventAggregate<KafkaDtoType>) {
        logger.info("Sending Aggregate Events")
        if (aggregate.events() != null) {
            aggregate.events()!!.second.forEach() {
                sendDomainEvent(aggregate.events()!!.first, it, aggregate.aggregateName)
            }
            aggregate.clearEvents()
        }
    }

}
