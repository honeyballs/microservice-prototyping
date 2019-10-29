package com.example.projectadministration.model.dto

import com.example.projectadministration.model.aggregates.Address
import com.example.projectadministration.model.aggregates.AggregateState
import com.example.projectadministration.model.aggregates.CustomerContact
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeName
import java.time.LocalDate

/**
 * DTO classes contained in events sent via Kafka.
 * These classes should mirror the aggregates completely but reduce references between aggregates to IDs.
 */

@JsonTypeName("projectKfk")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)
data class ProjectKfk(
        override val id: Long,
        val name: String,
        val description: String,
        val startDate: LocalDate,
        val projectedEndDate: LocalDate,
        val endDate: LocalDate?,
        val employees: MutableSet<Long>,
        val customer: Long,
        val deleted: Boolean,
        val state: AggregateState
): BaseKfkDto

@JsonTypeName("customerKfk")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)
data class CustomerKfk(override val id: Long, val customerName: String, val address: Address, val contact: CustomerContact, val deleted: Boolean, val state: AggregateState): BaseKfkDto