package com.example.employeeadministration.services.kafka

import com.example.employeeadministration.model.DEPARTMENT_TOPIC_NAME
import com.example.employeeadministration.model.EMPLOYEE_TOPIC_NAME
import com.example.employeeadministration.model.POSITION_TOPIC_NAME
import com.example.employeeadministration.model.Position
import com.example.employeeadministration.repositories.EmployeeRepository
import com.example.employeeadministration.repositories.PositionRepository
import com.example.employeeadministration.services.EventHandler
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaHandler
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Service
import org.springframework.transaction.UnexpectedRollbackException
import org.springframework.transaction.annotation.Transactional

@Service
@KafkaListener(groupId = "EmployeeService", topics = [POSITION_TOPIC_NAME])
class KafkaPositionEventHandler(val positionRepository: PositionRepository): EventHandler {

    val logger = LoggerFactory.getLogger(KafkaPositionEventHandler::class.java)

//    @KafkaHandler
//    @Transactional
//    fun compensate(comp: PositionCompensation, ack: Acknowledgment) {
//        logger.info("Position Compensation received. Type: ${comp.type}, Id: ${comp.position.id}")
//        val position = comp.position
//        try {
//            when (comp.type) {
//                EventType.CREATE -> {
//                    val pos = positionRepository.getByIdAndDeletedFalse(position.id!!).orElseThrow()
//                    pos.deleted = true
//                    positionRepository.save(pos)
//                }
//                EventType.UPDATE -> {
//                    val pos = Position(position.id, position.title, position.minHourlyWage, position.maxHourlyWage, position.deleted)
//                    positionRepository.save(pos)
//                }
//                EventType.DELETE -> {
//                    val pos = positionRepository.getByIdAndDeletedFalse(position.id!!).orElseThrow()
//                    pos.deleted = false
//                    positionRepository.save(pos)
//                }
//            }
//        } catch (exception: UnexpectedRollbackException) {
//            exception.printStackTrace()
//        } finally {
//            ack.acknowledge()
//        }
//    }

    @KafkaHandler(isDefault = true)
    fun defaultHandler(message: Any) {
        println("Message received: $message")
    }

}