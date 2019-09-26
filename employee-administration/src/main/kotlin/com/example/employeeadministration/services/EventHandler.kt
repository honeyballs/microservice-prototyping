package com.example.employeeadministration.services

import com.example.employeeadministration.model.events.*
import org.springframework.kafka.annotation.KafkaHandler

interface EventHandler {

    fun compensate(comp: DepartmentCreatedCompensation)

    fun compensate(comp: DepartmentChangedNameCompensation)

    fun compensate(comp: DepartmentDeletedCompensation)

    fun compensate(comp: PositionCreatedCompensation)

    fun compensate(comp: PositionChangedTitleCompensation)

    fun compensate(comp: PositionDeletedCompensation)

    fun compensate(comp: EmployeeCreatedCompensation)

    fun compensate(comp: EmployeeChangedNameCompensation)

    fun compensate(comp: EmployeeChangedJobPositionCompensation)

    fun compensate(comp: EmployeeSwitchedDepartmentCompensation)

    fun compensate(comp: EmployeeDeletedCompensation)
}