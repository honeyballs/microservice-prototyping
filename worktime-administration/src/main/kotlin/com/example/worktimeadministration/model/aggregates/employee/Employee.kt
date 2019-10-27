package com.example.worktimeadministration.model.aggregates.employee

import com.example.worktimeadministration.model.aggregates.AggregateState
import com.example.worktimeadministration.model.aggregates.project.Project
import javax.persistence.*

const val EMPLOYEE_AGGREGATE_NAME = "employee"

@Entity
data class Employee(
        @Id @GeneratedValue(strategy = GenerationType.AUTO) var dbId: Long?,
        val employeeId: Long,
        var firstname: String,
        var lastname: String,
        var companyMail: String,
        @ManyToMany(mappedBy = "employees", fetch = FetchType.LAZY)
        val projects: Set<Project>,
        var deleted: Boolean,
        var state: AggregateState
) {

    constructor(
            dbId: Long?,
            employeeId: Long,
            firstname: String,
            lastname: String,
            companyMail: String,
            deleted: Boolean,
            state: AggregateState
    ): this(dbId, employeeId, firstname, lastname, companyMail, emptySet(), deleted, state)

}