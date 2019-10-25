package com.example.worktimeadministration.model.aggregates.employee

import com.example.worktimeadministration.model.aggregates.AggregateState
import com.example.worktimeadministration.model.aggregates.project.Project
import javax.persistence.*

const val EMPLOYEE_AGGREGATE_NAME = "employee"

@Entity
data class Employee(
        @Id @GeneratedValue(strategy = GenerationType.AUTO) var dbId: Long?,
        val employeeId: Long,
        val firstname: String,
        val lastname: String,
        @ManyToMany(mappedBy = "employees", fetch = FetchType.LAZY)
        val projects: Set<Project>,
        val deleted: Boolean,
        val state: AggregateState
) {

    constructor(
            dbId: Long?,
            employeeId: Long,
            firstname: String,
            lastname: String,
            deleted: Boolean,
            state: AggregateState
    ): this(dbId, employeeId, firstname, lastname, emptySet(), deleted, state)

}