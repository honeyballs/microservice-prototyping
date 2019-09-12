package com.example.employeeadministration.repositories

import com.example.employeeadministration.model.Department
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Repository
@Transactional(readOnly = true)
interface DepartmentRepository: JpaRepository<Department, Long> {

    fun getById(id: Long): Optional<Department>
    fun getByName(name: String): Optional<Department>

}