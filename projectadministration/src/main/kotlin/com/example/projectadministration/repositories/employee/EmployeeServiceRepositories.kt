package com.example.projectadministration.repositories.employee

import com.example.projectadministration.model.aggregates.employee.Department
import com.example.projectadministration.model.aggregates.employee.Employee
import com.example.projectadministration.model.aggregates.employee.Position
import org.apache.kafka.common.protocol.types.Field
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface EmployeeRepository: JpaRepository<Employee, Long> {

    fun findByEmployeeId(id: Long): Optional<Employee>
    fun deleteByEmployeeId(id: Long)

    fun findAllByDeletedFalse(): List<Employee>
    fun findByEmployeeIdAndDeletedFalse(id: Long): Optional<Employee>
    fun findAllByEmployeeIdInAndDeletedFalse(ids: List<Long>): List<Employee>
    fun findAllByDepartmentNameAndDeletedFalse(name: String): List<Employee>
    fun findAllByPositionTitleAndDeletedFalse(title: String): List<Employee>

}

@Repository
interface PositionRepository: JpaRepository<Position, Long> {

    fun findByPositionId(id: Long): Optional<Position>

}

@Repository
interface DepartmentRepository: JpaRepository<Department, Long> {

    fun findByDepartmentId(id: Long): Optional<Department>

}