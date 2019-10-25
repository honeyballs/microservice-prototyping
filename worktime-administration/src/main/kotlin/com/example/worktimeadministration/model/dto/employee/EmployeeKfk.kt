package com.example.worktimeadministration.model.dto.employee

import com.example.worktimeadministration.model.aggregates.AggregateState
import com.example.worktimeadministration.model.dto.BaseKfkDto
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeName("employeeKfk")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)
@JsonIgnoreProperties(ignoreUnknown = true)
class EmployeeKfk(
        override val id: Long,
        val firstname: String,
        val lastname: String,
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