package com.example.employeeadministration.model

import org.springframework.data.annotation.Id

/**
 * Aggregate to handle combinations of departments and positions.
 * It does not make sense for an employee aggregate to handle these kinds of data so the employee aggregate will use this aggregate instead
 */
data class JobDetails(@Id var id: String?, val department: Department, val position: Position) {

    override fun equals(other: Any?): Boolean {
        if (other == null || other !is JobDetails) return false
        return id == other.id
    }

}