package com.example.worktimeadministration.repositories.employee

import com.example.worktimeadministration.model.aggregates.employee.Employee
import com.example.worktimeadministration.model.aggregates.project.Project
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface EmployeeRepository: JpaRepository<Employee, Long> {

    fun deleteByEmployeeId(id: Long)

    fun findByEmployeeId(id: Long): Optional<Employee>
    fun findAllByEmployeeIdIn(ids: List<Long>): List<Employee>

    fun findAllByDeletedFalse(): List<Employee>
    fun findByEmployeeIdAndDeletedFalse(id: Long): Optional<Employee>

}