package com.example.projectadministration.model.dto

import com.example.projectadministration.configurations.dateTimePattern
import com.example.projectadministration.model.aggregates.Address
import com.example.projectadministration.model.aggregates.CustomerContact
import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDate

data class CustomerDto(val id: Long?, val customerName: String, val address: Address, val contact: CustomerContact)

data class ProjectDto(
        val id: Long?,
        val name: String,
        val description: String,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = dateTimePattern) val startDate: LocalDate,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = dateTimePattern) val projectedEndDate: LocalDate,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = dateTimePattern) val endDate: LocalDate?,
        val projectEmployees: Set<ProjectEmployeeDto>,
        val customer: ProjectCustomerDto
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
