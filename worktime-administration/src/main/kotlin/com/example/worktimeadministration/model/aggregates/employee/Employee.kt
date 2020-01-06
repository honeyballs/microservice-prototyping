package com.example.worktimeadministration.model.aggregates.employee

import com.example.worktimeadministration.model.aggregates.AggregateState
import com.example.worktimeadministration.model.aggregates.project.Project
import java.io.Serializable
import javax.persistence.*

const val EMPLOYEE_AGGREGATE_NAME = "employee"

@Entity
data class Employee(
        @Id @GeneratedValue(strategy = GenerationType.AUTO, generator = "worktime_seq") var dbId: Long?,
        val employeeId: Long,
        var firstname: String,
        var lastname: String,
        var companyMail: String,
        var availableVacationHours: Int,
        var usedVacationHours: Int,
        var deleted: Boolean,
        var state: AggregateState
): Serializable