package com.example.projectadministration.model.aggregates

import com.example.projectadministration.model.dto.ProjectKfk
import com.example.projectadministration.model.aggregates.employee.Employee

import java.time.LocalDate
import javax.persistence.*

const val PROJECT_AGGREGATE_NAME = "project"

@Entity
data class Project(
        @Id @GeneratedValue(strategy = GenerationType.AUTO) var id: Long?,
        var name: String,
        var description: String,
        val startDate: LocalDate,
        var projectedEndDate: LocalDate,
        var endDate: LocalDate?,
        @ManyToMany(cascade = [CascadeType.REFRESH])
        @JoinTable(name = "project_employees",
                joinColumns = [JoinColumn(name = "project_id")],
                inverseJoinColumns = [JoinColumn(name = "employee_id")])
        var employees: MutableSet<Employee>,
        @ManyToOne(cascade = [CascadeType.REFRESH]) @JoinColumn(name = "fk_customer") val customer: Customer,
        var deleted: Boolean = false,
        override var aggregateName: String = PROJECT_AGGREGATE_NAME
): EventAggregate() {

    init {
        aggregateName = PROJECT_AGGREGATE_NAME
    }

    fun created() {
        if (id != null) {
            registerEvent(this.id!!, "created", null)
        }
    }

    fun delayProject(newProjectedDate: LocalDate) {
        val from = mapAggregateToKafkaDto()
        this.projectedEndDate = newProjectedDate
        registerEvent(this.id!!, "updated", from)
    }

    fun finishProject(endDate: LocalDate) {
        val from = mapAggregateToKafkaDto()
        this.endDate = endDate
        registerEvent(this.id!!, "updated", from)
    }

    fun updateProjectDescription(description: String) {
        val from = mapAggregateToKafkaDto()
        this.description = description
        registerEvent(this.id!!, "updated", from)
    }

    fun addEmployeeToProject(employee: Employee) {
        val from = mapAggregateToKafkaDto()
        this.employees.add(employee)
        registerEvent(this.id!!, "updated", from)
    }

    fun removeEmployeeFromProject(employee: Employee) {
        val from = mapAggregateToKafkaDto()
        this.employees.remove(employee)
        registerEvent(this.id!!, "updated", from)
    }

    fun changeEmployeesWorkingOnProject(employees: MutableSet<Employee>) {
        val from = mapAggregateToKafkaDto()
        this.employees = employees
        registerEvent(this.id!!, "updated", from)
    }

    fun deleteProject() {
        val from = mapAggregateToKafkaDto()
        this.deleted = true
        registerEvent(this.id!!, "deleted", from)
    }

    override fun mapAggregateToKafkaDto(): ProjectKfk {
        return ProjectKfk(this.id!!, this.name, this.description, this.startDate, this.projectedEndDate, this.endDate, this.employees.map { it.employeeId }.toMutableSet(), this.customer.id!!, this.deleted, this.state)
    }

}