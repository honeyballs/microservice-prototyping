package com.example.worktimeadministration.repositories.project

import com.example.worktimeadministration.model.aggregates.employee.Employee
import com.example.worktimeadministration.model.aggregates.project.Project
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface ProjectRepository: JpaRepository<Project, Long> {

    fun findAllByDeletedFalse(): List<Project>
    fun findByProjectIdAndDeletedFalse(id: Long): Optional<Project>
    fun findAllByEmployeesContainsAndDeletedFalse(employee: Employee): List<Project>
    fun findAllByProjectIdInAndDeletedFalse(ids: List<Long>): List<Project>
}