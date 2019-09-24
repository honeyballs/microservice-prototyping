package com.example.employeeadministration.services.kafka

import com.example.employeeadministration.configurations.TOPIC_NAME
import com.example.employeeadministration.model.events.*
import com.example.employeeadministration.repositories.DepartmentRepository
import com.example.employeeadministration.repositories.EmployeeRepository
import com.example.employeeadministration.repositories.PositionRepository
import org.springframework.kafka.annotation.KafkaHandler
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service
import java.util.concurrent.CountDownLatch

@Service
@KafkaListener(groupId = "EmployeeService1", topics = [TOPIC_NAME])
class KafkaEventHandler(val departmentRepository: DepartmentRepository, val positionRepository: PositionRepository, val employeeRepository: EmployeeRepository) {


    @KafkaHandler
    fun compensate(comp: DepartmentCreatedCompensation) {
        val department = departmentRepository.findById(comp.departmentId).orElseThrow()
        department.deleteDepartment()
        department.clearEvents()
        departmentRepository.save(department)
    }

    @KafkaHandler
    fun compensate(comp: DepartmentChangedNameCompensation) {
        val department = departmentRepository.findById(comp.oldDepartment.id!!).orElseThrow()
        department.renameDepartment(comp.oldDepartment.name)
        department.clearEvents()
        departmentRepository.save(department)
    }

    @KafkaHandler
    fun compensate(comp: DepartmentDeletedCompensation) {
        val department = departmentRepository.findById(comp.departmentId).orElseThrow()
        department.deleted = false
        departmentRepository.save(department)
    }

    @KafkaHandler
    fun compensate(comp: PositionCreatedCompensation) {
        val position = positionRepository.findById(comp.positionId).orElseThrow()
        position.deletePosition()
        position.clearEvents()
        positionRepository.save(position)
    }

    @KafkaHandler
    fun compensate(comp: PositionChangedTitleCompensation) {
        val position = positionRepository.findById(comp.positionId).orElseThrow()
        position.changePositionTitle(comp.title)
        position.clearEvents()
        positionRepository.save(position)
    }

    @KafkaHandler
    fun compensate(comp: PositionDeletedCompensation) {
        val position = positionRepository.findById(comp.positionId).orElseThrow()
        position.deleted = false
        positionRepository.save(position)
    }

    @KafkaHandler
    fun compensate(comp: EmployeeCreatedCompensation) {
        val employee = employeeRepository.findById(comp.employeeId).orElseThrow()
        employee.deleteEmployee()
        employee.clearEvents()
        employeeRepository.save(employee)
    }

    @KafkaHandler
    fun compensate(comp: EmployeeChangedNameCompensation) {
        val employee = employeeRepository.findById(comp.employeeId).orElseThrow()
        employee.changeName(comp.firstname, comp.lastname)
        employee.clearEvents()
        employeeRepository.save(employee)
    }

    @KafkaHandler
    fun compensate(comp: EmployeeChangedJobPositionCompensation) {
        val employee = employeeRepository.findById(comp.employeeId).orElseThrow()
        val position = positionRepository.findById(comp.positionId).orElseThrow()
        employee.changeJobPosition(position, comp.oldWage)
        employee.clearEvents()
        employeeRepository.save(employee)
    }

    @KafkaHandler
    fun compensate(comp: EmployeeSwitchedDepartmentCompensation) {
        val employee = employeeRepository.findById(comp.employeeId).orElseThrow()
        val department = departmentRepository.findById(comp.departmentId).orElseThrow()
        employee.moveToAnotherDepartment(department)
        employee.clearEvents()
        employeeRepository.save(employee)
    }

    @KafkaHandler
    fun compensate(comp: EmployeeDeletedCompensation) {
        val employee = employeeRepository.findById(comp.employeeId).orElseThrow()
        employee.deleted = false
        employeeRepository.save(employee)
    }


    @KafkaHandler(isDefault = true)
    fun defaultHandler(message: Any) {
        println("Message received: $message")
    }

}