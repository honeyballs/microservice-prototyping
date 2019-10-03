package com.example.projectadministration.model

import java.time.LocalDate

data class CustomerDto(val id: Long?, val customerName: String, val address: Address, val contact: CustomerContact)

data class ProjectDto(
        val id: Long?,
        val name: String,
        val description: String,
        val startDate: LocalDate,
        val projectedEndDate: LocalDate,
        val endDate: LocalDate?,
        val projectOwner: ProjectOwnerDto,
        val customer: ProjectCustomerDto
)

data class ProjectOwnerDto(val id: Long, val firstname: String, val lastname: String, val mail: String)

data class ProjectCustomerDto(val id: Long, val customerName: String)

data class EmployeeDto(
    val id: Long?,
    val firstname: String,
    val lastname: String,
    val department: String,
    val position: String,
    val companyMail: String
)
