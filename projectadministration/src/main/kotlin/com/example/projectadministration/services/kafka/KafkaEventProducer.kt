package com.example.projectadministration.services.kafka

import com.example.projectadministration.model.aggregates.EventAggregate
import com.example.projectadministration.model.events.Event
import com.example.projectadministration.repositories.SagaRepository
import com.example.projectadministration.services.EventProducer
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.clients.producer.ProducerRecord
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

/**
 * Service which sends Domain Events to Kafka.
 */
@Service
class KafkaEventProducer(
        val kafkaTemplate: KafkaTemplate<Long, Event>,
        // Use this template when sending aggregate events.
        // The other template is used for sending within a listening container
        // See KafkaConfiguration
        val producingOnlyTemplate: KafkaTemplate<Long, Event>
) : EventProducer {

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
     */
    override fun sendEventsOfAggregate(aggregate: EventAggregate) {
        logger.info("Sending Aggregate Events")
        if (aggregate.events() != null) {
            producingOnlyTemplate.executeInTransaction { operation ->
                aggregate.events()!!.second
                        .map { ProducerRecord<Long, Event>(aggregate.aggregateName, aggregate.id, it) }
                        .forEach { operation.send(it) }
            }
            aggregate.clearEvents()
        }
    }

}
