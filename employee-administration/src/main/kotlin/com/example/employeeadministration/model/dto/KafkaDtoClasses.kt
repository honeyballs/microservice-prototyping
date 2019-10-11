package com.example.employeeadministration.model.dto

import com.example.employeeadministration.model.valueobjects.Address
import com.example.employeeadministration.model.valueobjects.BankDetails
import com.example.employeeadministration.model.valueobjects.CompanyMail
import java.math.BigDecimal
import java.time.LocalDate

/**
 * DTO classes contained in events sent via Kafka.
 * These classes should mirror the aggregates completely but reduce references between aggregates to IDs.
 */

data class DepartmentKfk(val id: Long, val name: String, val deleted: Boolean)

data class PositionKfk(val id: Long, val title: String, val minHourlyWage: BigDecimal, val maxHourlyWage: BigDecimal, val deleted: Boolean)

data class EmployeeKfk(val id: Long,
                       val firstname: String,
                       val lastname: String,
                       val birthday: LocalDate,
                       val address: Address,
                       val bankDetails: BankDetails,
                       val department: Long,
                       val position: Long,
                       val hourlyRate: BigDecimal,
                       val companyMail: CompanyMail?,
                       val deleted: Boolean)
