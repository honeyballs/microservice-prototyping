package com.example.worktimeadministration.model.aggregates

import com.example.worktimeadministration.model.aggregates.employee.Employee
import com.example.worktimeadministration.model.aggregates.project.Project
import com.example.worktimeadministration.model.dto.BaseKfkDto
import com.example.worktimeadministration.model.dto.WorktimeEntryKfk
import org.apache.kafka.common.protocol.types.Field
import java.io.Serializable
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
        var deleted: Boolean = false,
        override var aggregateName: String = WORKTIME_AGGREGATE_NAME
): EventAggregate(), Serializable {

    init {
        if (type == EntryType.WORK && !isPauseSufficient(calculateTimespan(startTime, endTime))) {
            throw Exception("Insufficient Pause time")
        } else if (type == EntryType.VACATION && !employeeHasEnoughVacationHours(calculateTimespan(startTime, endTime))) {
            throw Exception("Not enough vacation hours")
        }
        if (!timeFitsWithinProjectSpan(startTime) || !timeFitsWithinProjectSpan(endTime)) {
            throw Exception("Timeframe not within project dates")
        }
    }

    fun created() {
        if (id != null) {
            registerEvent(this.id!!, "created", null)
        }
    }

    @Throws(Exception::class)
    fun adjustStartTime(newStartTime: LocalDateTime) {
        if (type == EntryType.WORK && !isPauseSufficient(calculateTimespan(newStartTime, endTime))) {
            throw Exception("Insufficient Pause time")
        } else if (type == EntryType.VACATION && !employeeHasEnoughVacationHours(calculateTimespan(newStartTime, endTime))) {
            throw Exception("Not enough vacation hours")
        }
        if (!timeFitsWithinProjectSpan(newStartTime) || !timeFitsWithinProjectSpan(endTime)) {
            throw Exception("Timeframe not within project dates")
        }
        val from = mapAggregateToKafkaDto()
        startTime = newStartTime
        registerEvent(this.id!!, "updated", from)
    }

    @Throws(Exception::class)
    fun adjustEndTime(newEndTime: LocalDateTime) {
        if (type == EntryType.WORK && !isPauseSufficient(calculateTimespan(startTime, newEndTime))) {
            throw Exception("Insufficient Pause time")
        } else if (type == EntryType.VACATION && !employeeHasEnoughVacationHours(calculateTimespan(startTime, newEndTime))) {
            throw Exception("Not enough vacation hours")
        }
        if (!timeFitsWithinProjectSpan(startTime) || !timeFitsWithinProjectSpan(newEndTime)) {
            throw Exception("Timeframe not within project dates")
        }
        val from = mapAggregateToKafkaDto()
        endTime = newEndTime
        registerEvent(this.id!!, "updated", from)
    }

    fun changeProject(newProject: Project) {
        val from = mapAggregateToKafkaDto()
        this.project = project
        if (!timeFitsWithinProjectSpan(startTime) || !timeFitsWithinProjectSpan(endTime)) {
            throw Exception("Timeframe not within project dates")
        }
        registerEvent(this.id!!, "updated", from)
    }

    fun changeDescription(newDescription: String) {
        val from = mapAggregateToKafkaDto()
        this.description = description
        registerEvent(this.id!!, "updated", from)
    }

    fun adjustPauseTime(pauseTimeInMinutes: Int) {
        if (isPauseSufficient(calculateTimespan(startTime, endTime), pauseTimeInMinutes)) {
            val from = mapAggregateToKafkaDto()
            this.pauseTimeInMinutes = pauseTimeInMinutes
            registerEvent(this.id!!, "updated", from)
        } else {
            throw Exception("The pause time is not sufficient for the provided timeframe")
        }
    }

    fun deleteEntry() {
        val from = mapAggregateToKafkaDto()
        this.deleted = true
        registerEvent(this.id!!, "deleted", from)

    }

    fun isPauseSufficient(timespan: Int, pause: Int? = null): Boolean {
        return timespan in 8..9 && pause ?: pauseTimeInMinutes >= 30
                || timespan >= 10 && pause ?:pauseTimeInMinutes >= 60
    }

    fun timeFitsWithinProjectSpan(time: LocalDateTime): Boolean {
        return time.toLocalDate().isAfter(project.startDate) && time.toLocalDate().isBefore(project.endDate)
    }

    fun employeeHasEnoughVacationHours(timespan: Int): Boolean {
        return (employee.usedVacationHours + timespan) <= employee.availableVacationHours
    }

    fun calculateTimespan(startTime: LocalDateTime, endTime: LocalDateTime): Int {
        return startTime.until(endTime, ChronoUnit.HOURS).toInt()
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