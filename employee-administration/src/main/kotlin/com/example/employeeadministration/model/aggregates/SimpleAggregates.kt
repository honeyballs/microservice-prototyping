package com.example.employeeadministration.model.aggregates

import com.example.employeeadministration.model.dto.DepartmentKfk
import com.example.employeeadministration.model.dto.PositionKfk
import com.fasterxml.jackson.annotation.JsonCreator
import java.math.BigDecimal
import java.math.RoundingMode
import javax.persistence.*

const val DEPARTMENT_AGGREGATE_NAME = "department"

/**
 * Department Aggregate
 */
@Entity
data class Department(@Id @GeneratedValue(strategy = GenerationType.AUTO) var id: Long?,
                      @Column(name = "department_name") var name: String,
                      var deleted: Boolean = false
): EventAggregate() {

    init {
        aggregateName = DEPARTMENT_AGGREGATE_NAME
    }

    fun created() {
        if (id != null) {
            registerEvent(this.id!!, "created", null)
        }
    }

    fun renameDepartment(name: String) {
        val from = mapAggregateToKafkaDto()
        this.name = name
        registerEvent(this.id!!, "updated", from)
    }

    fun deleteDepartment() {
        val from = mapAggregateToKafkaDto()
        deleted = true
        registerEvent(this.id!!, "deleted", from)
    }

    override fun mapAggregateToKafkaDto(): DepartmentKfk {
        return DepartmentKfk(this.id!!, this.name, this.deleted, this.state)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Department) return false

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }


}


const val POSITION_AGGREGATE_NAME = "position"

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
        aggregateName = POSITION_AGGREGATE_NAME
    }

    fun created() {
        if (id != null) {
            registerEvent(this.id!!, "created", null)
        }
    }

    fun changePositionTitle(title: String) {
        val from = mapAggregateToKafkaDto()
        this.title = title
        registerEvent(this.id!!, "updated", from)
    }

    fun adjustWageRange(min: BigDecimal?, max: BigDecimal?) {
        val from = mapAggregateToKafkaDto()
        minHourlyWage = min ?: minHourlyWage
        maxHourlyWage = max ?: maxHourlyWage
        registerEvent(this.id!!, "updated", from)
    }

    fun deletePosition() {
        val from = mapAggregateToKafkaDto()
        deleted = true
        registerEvent(this.id!!, "deleted", from)
    }

    override fun mapAggregateToKafkaDto(): PositionKfk {
        return PositionKfk(this.id!!, this.title, this.minHourlyWage, this.maxHourlyWage, this.deleted, this.state)
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

