package com.example.employeeadministration.model

import java.math.BigDecimal
import java.math.RoundingMode
import javax.persistence.*
import kotlin.math.min

/**
 * Department Aggregate
 */
@Entity
data class Department(@Id @GeneratedValue(strategy = GenerationType.AUTO) var id: Long?,
                      @Column(name = "department_name") var name: String) {

    fun renameDepartment(name: String) {
        this.name = name
    }

}

/**
 * Position aggregate
 */
@Entity
data class Position constructor (@Id @GeneratedValue(strategy = GenerationType.AUTO) var id: Long?,
                                 var title: String,
                                 private var _minHourlyWage: BigDecimal,
                                 private var _maxHourlyWage: BigDecimal) {

    var minHourlyWage: BigDecimal
        get() = _minHourlyWage
        set(value) {
            _minHourlyWage = value.setScale(2, RoundingMode.HALF_UP)
        }

    var maxHourlyWage: BigDecimal
        get() = _maxHourlyWage
        set(value) {
            _maxHourlyWage = _maxHourlyWage.setScale(2, RoundingMode.HALF_UP)
        }

    init {
        minHourlyWage = minHourlyWage.setScale(2, RoundingMode.HALF_UP)
        maxHourlyWage = maxHourlyWage.setScale(2, RoundingMode.HALF_UP)
    }

    fun changePositionTitle(title: String) {
        this.title = title
    }

    fun adjustWageRange(min: BigDecimal?, max: BigDecimal?) {
        minHourlyWage = min ?: minHourlyWage
        maxHourlyWage = max ?: maxHourlyWage
    }

}

/**
 * Function to check whether a rate is within the limits of a job position
 */
fun Position.isRateInRange(rateToCheck: BigDecimal): Boolean {
    return rateToCheck in this.minHourlyWage..this.maxHourlyWage
}
