package com.example.projectadministration.services.kafka


import com.example.projectadministration.configurations.TOPIC_NAME
import com.example.projectadministration.model.events.Event
import com.example.projectadministration.model.events.EventAggregate
import com.example.projectadministration.services.EventProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

/**
 * Service which sends Domain Events to Kafka.
 */
@Service
class KafkaEventProducer(val kafkaTemplate: KafkaTemplate<Long, Event>): EventProducer {

    /**
     * Send a domain Event.
     *
     * @param key This key should be the id of the aggregate the event is concerning.
     * This key guarantees that all events of an aggregate are kept within the same partition in the correct order in Kafka.
     */
    fun sendDomainEvent(key: Long, event: Event) {
        val record = ProducerRecord<Long, Event>(TOPIC_NAME, key, event)
        val result = kafkaTemplate.send(record)
    }

    /**
     * Function used to send all events which occurred on an aggregate.
     * Clears the events after sending.
     * TODO: Wait for success before clearing?
     */
    override fun sendEventsOfAggregate(aggregate: EventAggregate) {
        if (aggregate.events() != null) {
            aggregate.events()!!.second.forEach() {
                sendDomainEvent(aggregate.events()!!.first, it)
            }
            aggregate.clearEvents()
        }
    }

}