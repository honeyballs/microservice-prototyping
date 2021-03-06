package com.example.projectadministration.model.dto

import com.example.projectadministration.configurations.datePattern
import com.example.projectadministration.configurations.dateTimePattern
import com.example.projectadministration.model.aggregates.Address
import com.example.projectadministration.model.aggregates.AggregateState
import com.example.projectadministration.model.aggregates.CustomerContact
import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate

@JsonIgnoreProperties(value = ["state"], allowGetters = true)
data class CustomerDto(
        val id: Long?,
        val customerName: String,
        val address: Address,
        val contact: CustomerContact,
        @JsonProperty("state") val state: AggregateState?
)

@JsonIgnoreProperties(value = ["state"], allowGetters = true)
data class ProjectDto(
        val id: Long?,
        val name: String,
        val description: String,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = datePattern) val startDate: LocalDate,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = datePattern) val projectedEndDate: LocalDate,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = datePattern) val endDate: LocalDate?,
        val projectEmployees: MutableSet<ProjectEmployeeDto>,
        val customer: ProjectCustomerDto,
        @JsonProperty("state") val state: AggregateState?
)

data class ProjectEmployeeDto(val id: Long, val firstname: String, val lastname: String, val mail: String)

data class ProjectCustomerDto(val id: Long, val customerName: String)

data class EmployeeDto(
    val id: Long,
    val firstname: String,
    val lastname: String,
    val department: String,
    val position: String,
    val companyMail: String
)
