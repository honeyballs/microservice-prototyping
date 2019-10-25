package com.example.worktimeadministration.model.aggregates

import com.example.worktimeadministration.model.aggregates.employee.Employee
import com.example.worktimeadministration.model.aggregates.project.Project
import com.example.worktimeadministration.model.dto.BaseKfkDto
import com.example.worktimeadministration.model.dto.WorktimeEntryKfk
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import javax.persistence.*

const val WORKTIME_AGGREGATE_NAME = "worktime-entry"

@Entity
data class WorktimeEntry(
        @Id @GeneratedValue(strategy = GenerationType.AUTO) var id: Long?,
        var startTime: LocalDateTime,
        var endTime: LocalDateTime,
        var pauseTimeInMinutes: Int = 0,
        @ManyToOne(cascade = [CascadeType.REFRESH, CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH])
        @JoinColumn(name="project_id")
        var project: Project,
        @ManyToOne(cascade = [CascadeType.REFRESH, CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH])
        @JoinColumn(name="employee_id")
        val employee: Employee,
        var description: String,
        var type: EntryType,
        var deleted: Boolean = false
): EventAggregate() {

    init {
        if (type == EntryType.WORK && !isPauseSufficient(startTime, endTime)) {
            throw Exception("Insufficient Pause time")
        }
        aggregateName = WORKTIME_AGGREGATE_NAME
    }

    fun created() {
        if (id != null) {
            registerEvent(this.id!!, "created", null)
        }
    }

    @Throws(Exception::class)
    fun adjustStartTime(newStartTime: LocalDateTime) {
        if (type == EntryType.WORK && !isPauseSufficient(newStartTime, endTime)) {
            throw Exception("Insufficient Pause time")
        }
        val from = mapAggregateToKafkaDto()
        startTime = newStartTime
        registerEvent(this.id!!, "updated", from)
    }

    @Throws(Exception::class)
    fun adjustEndTime(newEndTime: LocalDateTime) {
        if (type == EntryType.WORK && !isPauseSufficient(startTime, newEndTime)) {
            throw Exception("Insufficient Pause time")
        }
        val from = mapAggregateToKafkaDto()
        endTime = newEndTime
        registerEvent(this.id!!, "updated", from)
    }

    fun changeProject(newProject: Project) {
        val from = mapAggregateToKafkaDto()
        this.project = project
        registerEvent(this.id!!, "updated", from)
    }

    fun changeDescription(newDescription: String) {
        val from = mapAggregateToKafkaDto()
        this.description = description
        registerEvent(this.id!!, "updated", from)
    }

    fun deleteEntry() {
        val from = mapAggregateToKafkaDto()
        this.deleted = true
        registerEvent(this.id!!, "deleted", from)

    }

    fun isPauseSufficient(startTime: LocalDateTime, endTime: LocalDateTime): Boolean {
        val timespan = startTime.until(endTime, ChronoUnit.HOURS)
        return timespan in 8..9 && pauseTimeInMinutes >= 30
                || timespan >= 10 && pauseTimeInMinutes >= 60
    }

    override fun mapAggregateToKafkaDto(): WorktimeEntryKfk {
        return WorktimeEntryKfk(id!!, startTime, endTime, pauseTimeInMinutes, project.projectId, employee.employeeId, description, type, deleted, state)
    }

}

enum class EntryType(type: String) {
    WORK("WORK"),
    VACATION("VACATION"),
    SICK("SICK")
}