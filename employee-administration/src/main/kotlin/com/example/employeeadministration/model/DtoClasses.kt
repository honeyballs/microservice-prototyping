package com.example.employeeadministration.model

import java.math.BigDecimal
import java.time.LocalDate

/**
 * This file collects DTO classes to fulfill requests. These DTO's only have immutable fields because all data changes are performed on entities
 */

data class JobDetailsDto(val id: Long?, val department: Department, val position: Position)

data class EmployeeDto(val id: Long?,
                       val firstname: String,
                       val lastname: String,
                       val birthday: LocalDate,
                       val address: Address,
                       val bankDetails: BankDetails,
                       val jobDetails: JobDetails,
                       val hourlyRate: BigDecimal,
                       val companyMail: CompanyMail?)