package com.example.employeeadministration.model

import com.example.employeeadministration.model.events.*
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
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
                      @Column(name = "department_name") var name: String,
                      var deleted: Boolean = false): EventAggregate() {

    fun renameDepartment(name: String) {
        val compensation = DepartmentChangedNameCompensation(this)
        this.name = name
        registerEvent(DepartmentChangedNameEvent(this, compensation))
    }

    fun deleteDepartment() {
        deleted = true
        registerEvent(DepartmentDeletedEvent(id!!, DepartmentDeletedCompensation(id!!)))
    }

}

/**
 * Position aggregate
 */
@Entity
class Position @JsonCreator constructor (@Id @GeneratedValue(strategy = GenerationType.AUTO) var id: Long?,
                                 var title: String,
                                 minHourlyWage: BigDecimal,
                                 maxHourlyWage: BigDecimal,
                                 var deleted: Boolean = false): EventAggregate() {

    var minHourlyWage: BigDecimal = minHourlyWage.setScale(2, RoundingMode.HALF_UP)
        set(value) {
            field = value.setScale(2, RoundingMode.HALF_UP)
        }

    var maxHourlyWage: BigDecimal = maxHourlyWage.setScale(2, RoundingMode.HALF_UP)
        set(value) {
            field = value.setScale(2, RoundingMode.HALF_UP)
        }

    fun changePositionTitle(title: String) {
        val compensation = PositionChangedTitleCompensation(this.id!!, this.title)
        this.title = title
        registerEvent(PositionChangedTitleEvent(id!!, this.title, compensation))
    }

    fun adjustWageRange(min: BigDecimal?, max: BigDecimal?) {
        minHourlyWage = min ?: minHourlyWage
        maxHourlyWage = max ?: maxHourlyWage
    }

    fun deletePosition() {
        deleted = true
        registerEvent(PositionDeletedEvent(id!!, PositionDeletedCompensation(this.id!!)))
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Position) return false

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }


}

/**
 * Function to check whether a rate is within the limits of a job position
 */
fun Position.isRateInRange(rateToCheck: BigDecimal): Boolean {
    return rateToCheck in this.minHourlyWage..this.maxHourlyWage
}

