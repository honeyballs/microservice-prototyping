package com.example.employeeadministration.kafka

import com.example.employeeadministration.configurations.TOPIC_NAME
import com.example.employeeadministration.model.events.DomainEvent
import org.apache.kafka.clients.producer.ProducerRecord
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import org.springframework.util.concurrent.ListenableFutureCallback

@Service
class EventProducer(val kafkaTemplate: KafkaTemplate<String, DomainEvent>) {

    fun sendDomainEvent(event: DomainEvent) {
        val record = ProducerRecord<String, DomainEvent>(TOPIC_NAME, event.id, event)
        val result = kafkaTemplate.send(record)
    }

}
