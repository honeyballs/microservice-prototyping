package com.example.employeeadministration.model.dto

import com.example.employeeadministration.configurations.datePattern
import com.example.employeeadministration.configurations.dateTimePattern
import com.example.employeeadministration.model.aggregates.AggregateState
import com.example.employeeadministration.model.valueobjects.Address
import com.example.employeeadministration.model.valueobjects.BankDetails
import com.example.employeeadministration.model.valueobjects.CompanyMail
import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal
import java.time.LocalDate

/**
 * This file collects DTO classes to fulfill requests. These DTO's only have immutable fields because all data changes are performed on entities
 */
@JsonIgnoreProperties(value = ["state"], allowGetters = true)
data class DepartmentDto(val id: Long?, val name: String, @JsonProperty("state") val state: AggregateState?)

@JsonIgnoreProperties(value = ["state"], allowGetters = true)
data class PositionDto(val id: Long?, val title: String, val minHourlyWage: BigDecimal, val maxHourlyWage: BigDecimal, @JsonProperty("state") val state: AggregateState?)

@JsonIgnoreProperties(value = ["state"], allowGetters = true)
data class EmployeeDto(val id: Long?,
                       val firstname: String,
                       val lastname: String,
                       @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = datePattern) val birthday: LocalDate,
                       val address: Address,
                       val bankDetails: BankDetails,
                       val department: DepartmentDto,
                       val position: PositionDto,
                       val hourlyRate: BigDecimal,
                       val availableVacationHours: Int,
                       val companyMail: CompanyMail?,
                       @JsonProperty("state") val state: AggregateState?
)