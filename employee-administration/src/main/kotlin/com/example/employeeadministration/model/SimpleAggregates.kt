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
                      var deleted: Boolean = false): EventAggregate<DepartmentKfk>() {

    init {
        TOPIC_NAME = DEPARTMENT_TOPIC_NAME
    }

    fun created() {
        if (id != null) {
            registerEvent(this.id!!, DepartmentEvent(mapAggregateToKafkaDto(), DepartmentCompensation(mapAggregateToKafkaDto(), EventType.CREATE), EventType.CREATE))
        }
    }

    fun renameDepartment(name: String) {
        val compensation = DepartmentCompensation(mapAggregateToKafkaDto(), EventType.UPDATE)
        this.name = name
        registerEvent(this.id!!, DepartmentEvent(mapAggregateToKafkaDto(), compensation, EventType.UPDATE))
    }

    fun deleteDepartment() {
        deleted = true
        registerEvent(this.id!!, DepartmentEvent(mapAggregateToKafkaDto(), DepartmentCompensation(mapAggregateToKafkaDto(), EventType.DELETE), EventType.DELETE))
    }

    override fun mapAggregateToKafkaDto(): DepartmentKfk {
        return DepartmentKfk(this.id!!, this.name, this.deleted)
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


const val POSITION_TOPIC_NAME = "position"

/**
 * Position aggregate
 */
@Entity
class Position @JsonCreator constructor (@Id @GeneratedValue(strategy = GenerationType.AUTO) var id: Long?,
                                 var title: String,
                                 minHourlyWage: BigDecimal,
                                 maxHourlyWage: BigDecimal,
                                 var deleted: Boolean = false): EventAggregate<PositionKfk>() {

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
            registerEvent(this.id!!, PositionEvent(mapAggregateToKafkaDto(), PositionCompensation(mapAggregateToKafkaDto(), EventType.CREATE), EventType.CREATE))
        }
    }

    fun changePositionTitle(title: String) {
        val compensation = PositionCompensation(mapAggregateToKafkaDto(), EventType.UPDATE)
        this.title = title
        registerEvent(id !!, PositionEvent(mapAggregateToKafkaDto(), compensation, EventType.UPDATE))
    }

    fun adjustWageRange(min: BigDecimal?, max: BigDecimal?) {
        val compensation = PositionCompensation(mapAggregateToKafkaDto(), EventType.UPDATE)
        minHourlyWage = min ?: minHourlyWage
        maxHourlyWage = max ?: maxHourlyWage
        registerEvent(id !!, PositionEvent(mapAggregateToKafkaDto(), compensation, EventType.UPDATE))
    }

    fun deletePosition() {
        deleted = true
        registerEvent(this.id!!, PositionEvent(mapAggregateToKafkaDto(), PositionCompensation(mapAggregateToKafkaDto(), EventType.DELETE), EventType.DELETE))
    }

    override fun mapAggregateToKafkaDto(): PositionKfk {
        return PositionKfk(this.id!!, this.title, this.minHourlyWage, this.maxHourlyWage, this.deleted)
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

