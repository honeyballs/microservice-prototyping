package com.example.employeeadministration.services.kafka

import com.example.employeeadministration.model.DEPARTMENT_TOPIC_NAME
import com.example.employeeadministration.model.EMPLOYEE_TOPIC_NAME
import com.example.employeeadministration.model.events.DepartmentCompensation
import com.example.employeeadministration.model.events.EmployeeCompensation
import com.example.employeeadministration.model.events.EventType
import com.example.employeeadministration.repositories.EmployeeRepository
import com.example.employeeadministration.services.EventHandler
import org.springframework.kafka.annotation.KafkaHandler
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Service
import org.springframework.transaction.UnexpectedRollbackException
@Service
@KafkaListener(groupId = "EmployeeService", topics = [EMPLOYEE_TOPIC_NAME])
class KafkaEmployeeEventHandler(val employeeRepository: EmployeeRepository): EventHandler {

    @KafkaHandler
    fun compensate(comp: EmployeeCompensation, ack: Acknowledgment) {
        val employee = comp.employee
        try {
            when (comp.type) {
                EventType.CREATE -> {
                    val emp = employeeRepository.getByIdAndDeletedFalse(employee.id!!).orElseThrow()
                    employee.deleted = true
                    employeeRepository.save(emp)
                }
                EventType.UPDATE -> {
                    employeeRepository.save(employee)
                }
                EventType.DELETE -> {
                    val emp = employeeRepository.getByIdAndDeletedFalse(employee.id!!).orElseThrow()
                    emp.deleted = false
                    employeeRepository.save(emp)
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