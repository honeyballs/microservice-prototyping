package com.example.employeeadministration.repositories

import com.example.employeeadministration.model.Department
import com.example.employeeadministration.model.Employee
import com.example.employeeadministration.model.Position
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Repository
@Transactional(readOnly = true)
interface EmployeeRepository : JpaRepository<Employee, Long> {

    fun getAllByDepartment_Id(departmendId: Long): List<Employee>
    fun getAllByPosition_Id(positionId: Long): List<Employee>
    fun getAllByFirstnameContainingAndLastnameContaining(firstname: String, lastname: String): List<Employee>
    fun getById(id: Long): Optional<Employee>

}