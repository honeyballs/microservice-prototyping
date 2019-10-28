package com.example.worktimeadministration.model.aggregates.employee

import com.example.worktimeadministration.model.aggregates.AggregateState
import com.example.worktimeadministration.model.aggregates.project.Project
import java.io.Serializable
import javax.persistence.*

const val EMPLOYEE_AGGREGATE_NAME = "employee"

@Entity
data class Employee(
        @Id @GeneratedValue(strategy = GenerationType.AUTO) var dbId: Long?,
        val employeeId: Long,
        var firstname: String,
        var lastname: String,
        var companyMail: String,
        var availableVacationHours: Int,
        var usedVacationHours: Int,
        @ManyToMany(mappedBy = "employees", fetch = FetchType.LAZY)
        val projects: Set<Project>,
        var deleted: Boolean,
        var state: AggregateState
): Serializable {

    constructor(
            dbId: Long?,
            employeeId: Long,
            firstname: String,
            lastname: String,
            companyMail: String,
            availableVacationHours: Int,
            usedVacationHours: Int,
            deleted: Boolean,
            state: AggregateState
    ): this(dbId, employeeId, firstname, lastname, companyMail, availableVacationHours, usedVacationHours, emptySet(), deleted, state)

}