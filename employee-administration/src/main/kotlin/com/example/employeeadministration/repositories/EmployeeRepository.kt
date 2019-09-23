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

    fun getAllByDeletedFalse(): List<Employee>
    fun getAllByDepartment_IdAndDeletedFalse(departmendId: Long): List<Employee>
    fun getAllByPosition_IdAndDeletedFalse(positionId: Long): List<Employee>
    fun getAllByFirstnameContainingAndLastnameContainingAndDeletedFalse(firstname: String, lastname: String): List<Employee>
    fun getByIdAndDeletedFalse(id: Long): Optional<Employee>

}