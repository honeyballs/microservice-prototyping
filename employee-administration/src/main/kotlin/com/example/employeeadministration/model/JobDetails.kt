package com.example.employeeadministration.model

import javax.persistence.*

/**
 * Aggregate to handle combinations of departments and positions.
 * It does not make sense for an employee aggregate to handle these kinds of data so the employee aggregate will use this aggregate instead
 */
@Entity
data class JobDetails(@Id @GeneratedValue(strategy = GenerationType.AUTO) var id: Long?, @Embedded val department: Department, @Embedded val position: Position) {

    override fun equals(other: Any?): Boolean {
        if (other == null || other !is JobDetails) return false
        return id == other.id
    }

}