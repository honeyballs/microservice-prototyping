package com.example.employeeadministration.model.events

import com.example.employeeadministration.SERVICE_NAME
import com.example.employeeadministration.services.getResponseEventType
import com.fasterxml.jackson.annotation.*
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource
import org.springframework.core.io.support.PropertiesLoaderUtils
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import javax.xml.crypto.Data

const val DATE_TIME_PATTERN = "dd.MM.yyyy HH:mm:ss:SSS"

/**
 * Represents an event occurring in the domain.
 * It implements the base [Event] interface and contains a compensating event in case a participant is unable to process it.
 */
@JsonTypeName("domainEvent")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)
@JsonSubTypes(
//        JsonSubTypes.Type(value = EmployeeEvent::class, name = "employeeEvent"),
//        JsonSubTypes.Type(value = DepartmentEvent::class, name = "departmentEvent"),
//        JsonSubTypes.Type(value = PositionEvent::class, name = "positionEvent")
)
open class DomainEvent<DataType>(
        override val id: String,
        override val eventCreatedAt: String,
        override val type: String,
        val from: DataType?,
        val to: DataType,
        override val originatingServiceName: String = SERVICE_NAME) : Event {

    val responseEvents = mutableListOf<ResponseEvent>()

    init {
        responseEvents.add(ResponseEvent(getResponseEventType(type, false), id))
        responseEvents.add(ResponseEvent(getResponseEventType(type, true), id))
    }

    constructor(type: String, from: DataType?, to: DataType): this(UUID.randomUUID().toString(), LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)), type, from, to)

    fun addSpecificResponse(response: ResponseEvent) {
        responseEvents.add(response)
    }

}