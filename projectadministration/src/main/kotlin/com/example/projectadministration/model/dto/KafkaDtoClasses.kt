package com.example.projectadministration.model.dto

import com.example.projectadministration.model.aggregates.Address
import com.example.projectadministration.model.aggregates.CustomerContact
import java.time.LocalDate

/**
 * DTO classes contained in events sent via Kafka.
 * These classes should mirror the aggregates completely but reduce references between aggregates to IDs.
 */

data class ProjectKfk(
        val id: Long,
        val name: String,
        val description: String,
        val startDate: LocalDate,
        val projectedEndDate: LocalDate,
        val endDate: LocalDate?,
        val employees: Set<Long>,
        val customer: Long,
        val deleted: Boolean
)

data class CustomerKfk(val id: Long, val customerName: String, val address: Address, val contact: CustomerContact, val deleted: Boolean)