package com.example.employeeadministration.services.kafka

import com.example.employeeadministration.model.DEPARTMENT_TOPIC_NAME
import com.example.employeeadministration.model.Department
import com.example.employeeadministration.model.events.*
import com.example.employeeadministration.repositories.DepartmentRepository
import com.example.employeeadministration.repositories.EmployeeRepository
import com.example.employeeadministration.repositories.PositionRepository
import com.example.employeeadministration.services.EventHandler
import org.springframework.kafka.annotation.KafkaHandler
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Service
import org.springframework.transaction.UnexpectedRollbackException

@Service
@KafkaListener(groupId = "EmployeeService", topics = [DEPARTMENT_TOPIC_NAME])
class KafkaDepartmentEventHandler(val departmentRepository: DepartmentRepository): EventHandler {

    @KafkaHandler
    fun compensate(comp: DepartmentCompensation, ack: Acknowledgment) {
        val department = comp.department
        try {
            when (comp.type) {
                EventType.CREATE -> {
                    val dep = departmentRepository.getByIdAndDeletedFalse(department.id!!).orElseThrow()
                    dep.deleted = true
                    departmentRepository.save(dep)
                }
                EventType.UPDATE -> {
                    departmentRepository.save(department)
                }
                EventType.DELETE -> {
                    val dep = departmentRepository.getByIdAndDeletedFalse(department.id!!).orElseThrow()
                    dep.deleted = false
                    departmentRepository.save(dep)
                }
            }
        } catch (exception: UnexpectedRollbackException) {
            exception.printStackTrace()
        } finally {
            ack.acknowledge()
        }
    }

    @KafkaHandler(isDefault = true)
    fun defaultHandler(message: Any) {
        println("Message received: $message")
    }

}