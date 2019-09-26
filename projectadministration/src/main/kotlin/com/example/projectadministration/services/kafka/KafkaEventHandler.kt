package com.example.projectadministration.services.kafka

import com.example.projectadministration.services.EventHandler
import com.example.projectadministration.configurations.TOPIC_NAME
import org.springframework.kafka.annotation.KafkaHandler
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service

@Service
@KafkaListener(groupId = "ProjectService", topics = [TOPIC_NAME])
class KafkaEventHandler(): EventHandler {


    @KafkaHandler(isDefault = true)
    fun defaultHandler(message: Any) {
        println("Message received: $message")
    }

}