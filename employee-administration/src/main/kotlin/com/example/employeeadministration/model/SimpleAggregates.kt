package com.example.employeeadministration.model

import com.example.employeeadministration.model.events.*
import org.springframework.data.domain.AbstractAggregateRoot
import org.springframework.data.domain.AfterDomainEventPublication
import org.springframework.data.domain.DomainEvents
import java.math.BigDecimal
import java.math.RoundingMode
import javax.persistence.*
import kotlin.math.min

/**
 * Department Aggregate
 */
@Entity
data class Department(@Id @GeneratedValue(strategy = GenerationType.AUTO) var id: Long?,
                      @Column(name = "department_name") var name: String): EventAggregate() {

    fun renameDepartment(name: String) {
        this.name = name
        registerEvent(DepartmentChangedNameEvent(this))
    }

}

/**
 * Position aggregate
 */
@Entity
data class Position constructor (@Id @GeneratedValue(strategy = GenerationType.AUTO) var id: Long?,
                                 var title: String,
                                 private var _minHourlyWage: BigDecimal,
                                 private var _maxHourlyWage: BigDecimal): EventAggregate() {

    var minHourlyWage: BigDecimal
        get() = _minHourlyWage
        set(value) {
            _minHourlyWage = value.setScale(2, RoundingMode.HALF_UP)
        }

    var maxHourlyWage: BigDecimal
        get() = _maxHourlyWage
        set(value) {
            _maxHourlyWage = value.setScale(2, RoundingMode.HALF_UP)
        }

    init {
        minHourlyWage = minHourlyWage.setScale(2, RoundingMode.HALF_UP)
        maxHourlyWage = maxHourlyWage.setScale(2, RoundingMode.HALF_UP)
    }

    fun changePositionTitle(title: String) {
        this.title = title
        registerEvent(PositionChangedTitleEvent(this))
    }

    fun adjustWageRange(min: BigDecimal?, max: BigDecimal?) {
        minHourlyWage = min ?: minHourlyWage
        maxHourlyWage = max ?: maxHourlyWage
        registerEvent(PositionChangedRangeEvent(this))
    }

}

/**
 * Function to check whether a rate is within the limits of a job position
 */
fun Position.isRateInRange(rateToCheck: BigDecimal): Boolean {
    return rateToCheck in this.minHourlyWage..this.maxHourlyWage
}

