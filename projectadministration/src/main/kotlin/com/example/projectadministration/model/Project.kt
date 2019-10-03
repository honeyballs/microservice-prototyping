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
): EventAggregate() {

    init {
        TOPIC_NAME = PROJECT_TOPIC_NAME
    }

    fun created() {
        if (id != null) {
            registerEvent(this.id!!, ProjectEvent(this.copy(), ProjectCompensation(this.copy(), EventType.CREATE), EventType.CREATE))
        }
    }

    fun delayProject(newProjectedDate: LocalDate) {
        val compensation = ProjectCompensation(this.copy(), EventType.UPDATE)
        this.projectedEndDate = newProjectedDate
        registerEvent(this.id!!, ProjectEvent(this.copy(), compensation, EventType.UPDATE))
    }

    fun finishProject(endDate: LocalDate) {
        val compensation = ProjectCompensation(this.copy(), EventType.UPDATE)
        this.endDate = endDate
        registerEvent(this.id!!, ProjectEvent(this.copy(), compensation, EventType.UPDATE))
    }

    fun updateProjectDescription(description: String) {
        this.description = description
    }

    fun deleteProject() {
        val compensation = ProjectCompensation(this.copy(), EventType.DELETE)
        this.deleted = true
        registerEvent(this.id!!, ProjectEvent(this.copy(), compensation, EventType.DELETE))
    }

}