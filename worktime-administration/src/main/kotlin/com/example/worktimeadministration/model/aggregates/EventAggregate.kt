package com.example.worktimeadministration.model.aggregates

import com.example.worktimeadministration.model.dto.BaseKfkDto
import com.example.worktimeadministration.model.events.DomainEvent
import com.example.worktimeadministration.model.events.ResponseEvent
import com.example.worktimeadministration.model.events.getEventTypeFromProperties
import com.example.worktimeadministration.model.events.getResponseEventType
import javax.persistence.*

/**
 * Defines base functionality of an aggregate which emits events.
 * Aggregates have a state which controls whether changes can be made on it.
 */
@MappedSuperclass
@SequenceGenerator(name = "worktime_seq", sequenceName = "worktime_id_sequence")
abstract class EventAggregate(@Id @GeneratedValue(strategy = GenerationType.AUTO, generator = "worktime_seq") var id: Long?, var deleted: Boolean = false) {

    @Transient
    open lateinit var aggregateName: String

    // Since events need a key property corresponding to the id of an aggregate a pair is used to store them.
    @Transient
    private var events: Pair<Long, MutableList<DomainEvent>>? = null

    var state: AggregateState = AggregateState.PENDING

    fun registerEvent(aggregateId: Long, action: String, from: BaseKfkDto?, vararg additionalResponseEventTypes: String) {
        val eventType = getEventTypeFromProperties(aggregateName, action)
        // Always add a success and fail
        val successResponseType = getResponseEventType(eventType, false)
        val failureResponseType = getResponseEventType(eventType, true)
        val event = DomainEvent(eventType, from, mapAggregateToKafkaDto(), successResponseType, failureResponseType)
        additionalResponseEventTypes.forEach { event.addResponseEvent(it) }
        if (events == null) {
            events = Pair(aggregateId, ArrayList())
        }
        events!!.second.add(event)
        state = AggregateState.PENDING
    }

    fun events(): Pair<Long, MutableList<DomainEvent>>? {
        return events
    }

    fun clearEvents() {
        events = null
    }

    open fun created() {
        if (id != null) {
            registerEvent(this.id!!, "created", null)
        }
    }

    abstract fun mapAggregateToKafkaDto(): BaseKfkDto

    abstract fun deleteAggregate()

}

enum class AggregateState {
    PENDING, ACTIVE, FAILED
}