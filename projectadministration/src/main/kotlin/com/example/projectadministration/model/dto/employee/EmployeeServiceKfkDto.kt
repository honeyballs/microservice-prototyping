package com.example.projectadministration.model.dto.employee

import com.example.projectadministration.model.aggregates.AggregateState
import com.example.projectadministration.model.dto.BaseKfkDto
import com.fasterxml.jackson.annotation.*
import javax.persistence.*


/**
 * Contains the dtos received via Kafka.
 * These are reduced variations of the received dtos and contain only fields relevant for this service.
 * The JSON received from events is parsed into these classes.
 */

@JsonTypeName("departmentKfk")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)
@JsonIgnoreProperties(ignoreUnknown = true)
data class DepartmentKfk(override val id: Long, val name: String, val deleted: Boolean, val state: AggregateState): BaseKfkDto

@JsonTypeName("positionKfk")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)
@JsonIgnoreProperties(ignoreUnknown = true)
data class PositionKfk(override val id: Long, val title: String, val deleted: Boolean, val state: AggregateState): BaseKfkDto

@JsonTypeName("employeeKfk")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)
@JsonIgnoreProperties(ignoreUnknown = true)
class EmployeeKfk(
        override val id: Long,
        val firstname: String,
        val lastname: String,
        val department: Long,
        val position: Long,
        val deleted: Boolean,
        val state: AggregateState
): BaseKfkDto {

    lateinit var companyMail: String

    // Since mail is stored in a value object which isn't relevant for this service it is reduced to a string
    @JsonProperty("companyMail")
    fun unpackMail(mail: Map<String, Any>) {
        this.companyMail = mail["mail"] as String
    }

}