package com.example.employeeadministration.repositories

import com.example.employeeadministration.model.Department
import com.example.employeeadministration.model.Employee
import com.example.employeeadministration.model.Position
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface EmployeeRepository : JpaRepository<Employee, Long> {

    fun getAllByJobDetails_Department(department: Department): List<Employee>
    fun getAllByJobDetails_Position(position: Position): List<Employee>
    fun getAllByFirstnameContainingAndLastnameContaining(firstname: String, lastname: String): List<Employee>

}