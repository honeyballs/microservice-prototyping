package com.example.projectadministration.services

import com.example.projectadministration.model.events.EmployeeCreatedEvent
import com.example.projectadministration.model.events.PositionCreatedEvent
import org.springframework.kafka.annotation.KafkaHandler

interface EventHandler {

    fun handle(event: EmployeeCreatedEvent)
    fun handle(event: PositionCreatedEvent)

}