package com.example.employeeadministration.services.kafka

import com.example.employeeadministration.model.DEPARTMENT_TOPIC_NAME
import com.example.employeeadministration.model.EMPLOYEE_TOPIC_NAME
import com.example.employeeadministration.model.Employee
import com.example.employeeadministration.model.events.DepartmentCompensation
import com.example.employeeadministration.model.events.EmployeeCompensation
import com.example.employeeadministration.model.events.EventType
import com.example.employeeadministration.repositories.DepartmentRepository
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
@KafkaListener(groupId = "EmployeeService", topics = [EMPLOYEE_TOPIC_NAME])
class KafkaEmployeeEventHandler(val employeeRepository: EmployeeRepository, val departmentRepository: DepartmentRepository, val positionRepository: PositionRepository) : EventHandler {

    val logger = LoggerFactory.getLogger(KafkaEmployeeEventHandler::class.java)

    @KafkaHandler
    @Transactional
    fun compensate(comp: EmployeeCompensation, ack: Acknowledgment) {
        logger.info("Employee Compensation received. Type: ${comp.type}, Id: ${comp.employee.id}")
        val employee = comp.employee
        try {
            when (comp.type) {
                EventType.CREATE -> {
                    val emp = employeeRepository.getByIdAndDeletedFalse(employee.id!!).orElseThrow()
                    emp.deleted = true
                    employeeRepository.save(emp)
                }
                EventType.UPDATE -> {
                    val dep = departmentRepository.getByIdAndDeletedFalse(employee.department).orElseThrow()
                    val pos = positionRepository.getByIdAndDeletedFalse(employee.position).orElseThrow()
                    val emp = Employee(employee.id, employee.firstname, employee.lastname, employee.birthday, employee.address, employee.bankDetails, dep, pos, employee.hourlyRate, employee.companyMail, employee.deleted)
                    employeeRepository.save(emp)
                }
                EventType.DELETE -> {
                    val emp = employeeRepository.findById(employee.id).orElseThrow()
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