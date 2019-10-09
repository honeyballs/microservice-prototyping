package com.example.projectadministration.model.employee

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import org.apache.kafka.common.protocol.types.Field
import org.springframework.data.jpa.repository.EntityGraph
import javax.persistence.*

const val EMPLOYEE_TOPIC_NAME = "employee"
const val POSITION_TOPIC_NAME = "position"
const val DEPARTMENT_TOPIC_NAME = "department"


@Entity
data class Department(@Id @GeneratedValue(strategy = GenerationType.AUTO) var dbId: Long?, val departmentId: Long, var name: String, var deleted: Boolean = false)

@Entity
data class Position(@Id @GeneratedValue(strategy = GenerationType.AUTO) var dbId: Long?, val positionId: Long, var title: String, var deleted: Boolean = false)

@Entity
data class Employee(
        @Id @GeneratedValue(strategy = GenerationType.AUTO) var dbId: Long?,
        val employeeId: Long,
        var firstname: String,
        var lastname: String,
        @ManyToOne @JoinColumn(name = "fk_department") var department: Department,
        @ManyToOne @JoinColumn(name = "fk_position") var position: Position,
        var companyMail: String,
        var deleted: Boolean = false
)
