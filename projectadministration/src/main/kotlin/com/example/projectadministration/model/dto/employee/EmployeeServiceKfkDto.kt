package com.example.projectadministration.model.dto.employee

import com.example.projectadministration.model.aggregates.AggregateState
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import javax.persistence.*


/**
 * Contains the dtos received via Kafka.
 * These are reduced variations of the received dtos and contain only fields relevant for this service.
 * The JSON received from events is parsed into these classes.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
data class DepartmentKfk(val id: Long, val name: String, val deleted: Boolean, val state: AggregateState)

@JsonIgnoreProperties(ignoreUnknown = true)
data class PositionKfk(val id: Long, val title: String, val deleted: Boolean, val state: AggregateState)

@JsonIgnoreProperties(ignoreUnknown = true)
class EmployeeKfk(
        val id: Long,
        val firstname: String,
        val lastname: String,
        val department: Long,
        val position: Long,
        val deleted: Boolean,
        val state: AggregateState
) {

    lateinit var companyMail: String

    // Since mail is stored in a value object which isn't relevant for this service it is reduced to a string
    @JsonProperty("companyMail")
    fun unpackMail(mail: Map<String, Any>) {
        this.companyMail = mail["mail"] as String
    }

}