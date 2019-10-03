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

const val DEPARTMENT_TOPIC_NAME = "department"

/**
 * Department Aggregate
 */
@Entity
data class Department(@Id @GeneratedValue(strategy = GenerationType.AUTO) var id: Long?,
                      @Column(name = "department_name") var name: String,
                      var deleted: Boolean = false): EventAggregate() {

    init {
        TOPIC_NAME = DEPARTMENT_TOPIC_NAME
    }

    fun created() {
        if (id != null) {
            registerEvent(this.id!!, DepartmentEvent(this.copy(), DepartmentCompensation(this, EventType.CREATE), EventType.CREATE))
        }
    }

    fun renameDepartment(name: String) {
        val compensation = DepartmentCompensation(this.copy(), EventType.UPDATE)
        this.name = name
        registerEvent(this.id!!, DepartmentEvent(this.copy(), compensation, EventType.UPDATE))
    }

    fun deleteDepartment() {
        deleted = true
        registerEvent(this.id!!, DepartmentEvent(this.copy(), DepartmentCompensation(this, EventType.DELETE), EventType.DELETE))
    }

    fun copy(): Department {
        return Department(this.id, this.name, this.deleted)
    }

}


const val POSITION_TOPIC_NAME = "position"

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

    init {
        TOPIC_NAME = POSITION_TOPIC_NAME
    }

    fun created() {
        if (id != null) {
            registerEvent(this.id!!, PositionEvent(this.copy(), PositionCompensation(this.copy(), EventType.CREATE), EventType.CREATE))
        }
    }

    fun changePositionTitle(title: String) {
        val compensation = PositionCompensation(this.copy(), EventType.UPDATE)
        this.title = title
        registerEvent(id !!, PositionEvent(this.copy(), compensation, EventType.UPDATE))
    }

    fun adjustWageRange(min: BigDecimal?, max: BigDecimal?) {
        minHourlyWage = min ?: minHourlyWage
        maxHourlyWage = max ?: maxHourlyWage
    }

    fun deletePosition() {
        deleted = true
        registerEvent(this.id!!, PositionEvent(this.copy(), PositionCompensation(this.copy(), EventType.DELETE), EventType.DELETE))
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

    fun copy(): Position {
        return Position(this.id, this.title, this.minHourlyWage, this.maxHourlyWage, this.deleted)
    }

}

/**
 * Function to check whether a rate is within the limits of a job position
 */
fun Position.isRateInRange(rateToCheck: BigDecimal): Boolean {
    return rateToCheck in this.minHourlyWage..this.maxHourlyWage
}

