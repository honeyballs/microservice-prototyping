package com.example.employeeadministration.kafka

import com.example.employeeadministration.configurations.TOPIC_NAME
import com.example.employeeadministration.model.events.DomainEvent
import org.apache.kafka.clients.producer.ProducerRecord
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import org.springframework.util.concurrent.ListenableFutureCallback

@Service
class EventProducer(val kafkaTemplate: KafkaTemplate<Long, DomainEvent>) {

    /**
     * Send a domain Event.
     *
     * @param key This key should be the id of the aggregate the event is concerning.
     * This key guarantees that all events of an aggregate are kept within the same partition in the correct order in Kafka.
     */
    fun sendDomainEvent(key: Long, event: DomainEvent) {
        val record = ProducerRecord<Long, DomainEvent>(TOPIC_NAME, key, event)
        val result = kafkaTemplate.send(record)
    }

}
