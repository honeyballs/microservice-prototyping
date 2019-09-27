package com.example.projectadministration.model.employee

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.springframework.data.jpa.repository.EntityGraph
import javax.persistence.*

@Entity
class Department(@Id @GeneratedValue(strategy = GenerationType.AUTO) var dbId: Long?, val id: Long, var name: String, var deleted: Boolean = false)

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
class Position(@Id @GeneratedValue(strategy = GenerationType.AUTO) var dbId: Long?, val id: Long, var title: String, var deleted: Boolean = false)

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
class Employee(
        @Id @GeneratedValue(strategy = GenerationType.AUTO) var dbId: Long?,
        val id: Long,
        var firstname: String,
        var lastname: String,
        @ManyToOne @JoinColumn(name = "fk_department") var department: Department,
        @ManyToOne @JoinColumn(name = "fk_position") var position: Position,
        var companyMail: String,
        var deleted: Boolean = false
)
