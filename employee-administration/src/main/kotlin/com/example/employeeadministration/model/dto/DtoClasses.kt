package com.example.employeeadministration.model.dto

import com.example.employeeadministration.model.valueobjects.Address
import com.example.employeeadministration.model.valueobjects.BankDetails
import com.example.employeeadministration.model.valueobjects.CompanyMail
import java.math.BigDecimal
import java.time.LocalDate

/**
 * This file collects DTO classes to fulfill requests. These DTO's only have immutable fields because all data changes are performed on entities
 */

data class DepartmentDto(val id: Long?, val name: String)

data class PositionDto(val id: Long?, val title: String, val minHourlyWage: BigDecimal, val maxHourlyWage: BigDecimal)

data class EmployeeDto(val id: Long?,
                       val firstname: String,
                       val lastname: String,
                       val birthday: LocalDate,
                       val address: Address,
                       val bankDetails: BankDetails,
                       val department: DepartmentDto,
                       val position: PositionDto,
                       val hourlyRate: BigDecimal,
                       val companyMail: CompanyMail?)