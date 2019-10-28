package com.example.employeeadministration.model.dto

import com.example.employeeadministration.model.aggregates.AggregateState
import com.example.employeeadministration.model.valueobjects.Address
import com.example.employeeadministration.model.valueobjects.BankDetails
import com.example.employeeadministration.model.valueobjects.CompanyMail
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeName
import java.math.BigDecimal
import java.time.LocalDate

/**
 * DTO classes contained in events sent via Kafka.
 * These classes should mirror the aggregates completely but reduce references between aggregates to IDs.
 */

@JsonTypeName("departmentKfk")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)
data class DepartmentKfk(override val id: Long, val name: String, val deleted: Boolean, val state: AggregateState) : BaseKfkDto

@JsonTypeName("positionKfk")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)
data class PositionKfk(override val id: Long, val title: String, val minHourlyWage: BigDecimal, val maxHourlyWage: BigDecimal, val deleted: Boolean, val state: AggregateState) : BaseKfkDto

@JsonTypeName("employeeKfk")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)
data class EmployeeKfk(
        override val id: Long,
        val firstname: String,
        val lastname: String,
        val birthday: LocalDate,
        val address: Address,
        val bankDetails: BankDetails,
        val department: Long,
        val position: Long,
        val hourlyRate: BigDecimal,
        val companyMail: CompanyMail?,
        val availableVacationHours: Int,
        val deleted: Boolean,
        val state: AggregateState
) : BaseKfkDto
