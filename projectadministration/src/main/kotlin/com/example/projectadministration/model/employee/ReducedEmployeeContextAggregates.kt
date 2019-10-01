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
class Department(@Id @GeneratedValue(strategy = GenerationType.AUTO) @JsonIgnore var dbId: Long?, @JsonProperty("id") val departmentId: Long, var name: String, var deleted: Boolean = false)

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
class Position(@Id @GeneratedValue(strategy = GenerationType.AUTO) @JsonIgnore var dbId: Long?, @JsonProperty("id") val positionId: Long, var title: String, var deleted: Boolean = false)

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
class Employee(
        @Id @GeneratedValue(strategy = GenerationType.AUTO) @JsonIgnore var dbId: Long?,
        @JsonProperty("id")  val employeeId: Long,
        var firstname: String,
        var lastname: String,
        @ManyToOne @JoinColumn(name = "fk_department") var department: Department,
        @ManyToOne @JoinColumn(name = "fk_position") var position: Position,
        companyMail: String?,
        var deleted: Boolean = false
) {
    lateinit var companyMail: String

    init {
        if (companyMail != null) {
            this.companyMail = companyMail
        }
    }

    @JsonCreator
    constructor(dbId: Long?, employeeId: Long, firstname: String, lastname: String, department: Department, position: Position, deleted: Boolean = false) : this(dbId, employeeId, firstname, lastname, department, position, null)

    @JsonProperty("companyMail")
    fun unpackMail(mail: Map<String, Any>) {
        this.companyMail = mail["mail"] as String
    }

}
