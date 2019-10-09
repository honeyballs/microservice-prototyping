package com.example.projectadministration.model

import com.example.projectadministration.model.employee.Employee
import com.example.projectadministration.model.events.EventAggregate
import com.example.projectadministration.model.events.EventType
import com.example.projectadministration.model.events.ProjectCompensation
import com.example.projectadministration.model.events.ProjectEvent
import java.time.LocalDate
import java.time.LocalDateTime
import javax.persistence.*

const val PROJECT_TOPIC_NAME = "project"

@Entity
data class Project(
        @Id @GeneratedValue(strategy = GenerationType.AUTO) var id: Long?,
        var name: String,
        var description: String,
        val startDate: LocalDate,
        var projectedEndDate: LocalDate,
        var endDate: LocalDate?,
        @ManyToOne @JoinColumn(name = "fk_po") var projectOwner: Employee,
        @ManyToOne @JoinColumn(name = "fk_customer") val customer: Customer,
        var deleted: Boolean = false
): EventAggregate<ProjectKfk>() {

    init {
        TOPIC_NAME = PROJECT_TOPIC_NAME
    }

    fun created() {
        if (id != null) {
            registerEvent(this.id!!, ProjectEvent(mapAggregateToKafkaDto(), ProjectCompensation(mapAggregateToKafkaDto(), EventType.CREATE), EventType.CREATE))
        }
    }

    fun delayProject(newProjectedDate: LocalDate) {
        val compensation = ProjectCompensation(mapAggregateToKafkaDto(), EventType.UPDATE)
        this.projectedEndDate = newProjectedDate
        registerEvent(this.id!!, ProjectEvent(mapAggregateToKafkaDto(), compensation, EventType.UPDATE))
    }

    fun finishProject(endDate: LocalDate) {
        val compensation = ProjectCompensation(mapAggregateToKafkaDto(), EventType.UPDATE)
        this.endDate = endDate
        registerEvent(this.id!!, ProjectEvent(mapAggregateToKafkaDto(), compensation, EventType.UPDATE))
    }

    fun updateProjectDescription(description: String) {
        val compensation = ProjectCompensation(mapAggregateToKafkaDto(), EventType.UPDATE)
        this.description = description
        registerEvent(this.id!!, ProjectEvent(mapAggregateToKafkaDto(), compensation, EventType.UPDATE))
    }

    fun deleteProject() {
        val compensation = ProjectCompensation(mapAggregateToKafkaDto(), EventType.DELETE)
        this.deleted = true
        registerEvent(this.id!!, ProjectEvent(mapAggregateToKafkaDto(), compensation, EventType.DELETE))
    }

    override fun mapAggregateToKafkaDto(): ProjectKfk {
        return ProjectKfk(this.id!!, this.name, this.description, this.startDate, this.projectedEndDate, this.endDate, this.projectOwner.employeeId, this.customer.id!!, this.deleted)
    }

}