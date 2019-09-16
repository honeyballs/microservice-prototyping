package com.example.employeeadministration.model.events

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * Defines possible compensation actions.
 */
enum class CompensatingActionType {
    CREATE, UPDATE, DELETE
}

/**
 * Baseclass for compensation events. Since the compensation is in itself an event it implements the [Event] interface.
 * It contains the id of the event that needs to be compensated and what type of compensation has to be done.
 * Compensations extending this class provide the necessary data to execute the compensation.
 */
open class CompensatingAction(
        override val id: String,
        override var eventCreatedAt: String = "",
        var originalEventId: String? = "",
        val type: CompensatingActionType) : Event {

    constructor(type: CompensatingActionType): this(UUID.randomUUID().toString(), "", "", type)

    fun rollbackOccurredAt(time: LocalDateTime) {
        eventCreatedAt = time.format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN))
    }

}