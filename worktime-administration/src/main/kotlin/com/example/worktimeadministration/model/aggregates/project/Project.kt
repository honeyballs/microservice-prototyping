package com.example.worktimeadministration.model.aggregates.project

import com.example.worktimeadministration.model.aggregates.AggregateState
import com.example.worktimeadministration.model.aggregates.employee.Employee
import java.time.LocalDate
import javax.persistence.*

const val PROJECT_AGGREGATE_NAME = "project"

@Entity
data class Project(
        @Id @GeneratedValue(strategy = GenerationType.AUTO) var dbId: Long?,
        val projectId: Long,
        val name: String,
        val description: String,
        val startDate: LocalDate,
        val projectedEndDate: LocalDate,
        val endDate: LocalDate?,
        @ManyToMany(cascade = [CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH], fetch = FetchType.LAZY)
        @JoinTable(name = "project_employees",
                joinColumns = [JoinColumn(name = "project_id", referencedColumnName = "projectId")],
                inverseJoinColumns = [JoinColumn(name = "employee_id", referencedColumnName = "employeeId")])
        val employees: Set<Employee>,
        val deleted: Boolean,
        val state: AggregateState
) {
}