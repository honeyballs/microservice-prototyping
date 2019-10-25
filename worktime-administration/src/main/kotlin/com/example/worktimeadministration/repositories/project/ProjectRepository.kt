package com.example.worktimeadministration.repositories.project

import com.example.worktimeadministration.model.aggregates.employee.Employee
import com.example.worktimeadministration.model.aggregates.project.Project
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface ProjectRepository: JpaRepository<Project, Long> {

    fun findByProjectId(id: Long): Optional<Project>
    fun findByEmployeesContains(employee: Employee): List<Project>
    fun findAllByProjectIdIn(ids: List<Long>): List<Project>
}