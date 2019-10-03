package com.example.employeeadministration.services.kafka

import com.example.employeeadministration.model.DEPARTMENT_TOPIC_NAME
import com.example.employeeadministration.model.EMPLOYEE_TOPIC_NAME
import com.example.employeeadministration.model.POSITION_TOPIC_NAME
import com.example.employeeadministration.model.events.DepartmentCompensation
import com.example.employeeadministration.model.events.EmployeeCompensation
import com.example.employeeadministration.model.events.EventType
import com.example.employeeadministration.model.events.PositionCompensation
import com.example.employeeadministration.repositories.EmployeeRepository
import com.example.employeeadministration.repositories.PositionRepository
import com.example.employeeadministration.services.EventHandler
import org.springframework.kafka.annotation.KafkaHandler
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service
import org.springframework.transaction.UnexpectedRollbackException
@Service
@KafkaListener(groupId = "EmployeeService", topics = [POSITION_TOPIC_NAME])
class KafkaPositionEventHandler(val positionRepository: PositionRepository): EventHandler {

    @KafkaHandler
    fun compensate(comp: PositionCompensation) {
        val position = comp.position
        try {
            when (comp.type) {
                EventType.CREATE -> {
                    val pos = positionRepository.getByIdAndDeletedFalse(position.id!!).orElseThrow()
                    position.deleted = true
                    positionRepository.save(pos)
                }
                EventType.UPDATE -> {
                    positionRepository.save(position)
                }
                EventType.DELETE -> {
                    val pos = positionRepository.getByIdAndDeletedFalse(position.id!!).orElseThrow()
                    pos.deleted = false
                    positionRepository.save(pos)
                }
            }
        } catch (exception: UnexpectedRollbackException) {
            exception.printStackTrace()
        }
    }

    @KafkaHandler(isDefault = true)
    fun defaultHandler(message: Any) {
        println("Message received: $message")
    }

}