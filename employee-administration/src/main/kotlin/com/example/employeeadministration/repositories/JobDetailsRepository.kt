package com.example.employeeadministration.repositories

import com.example.employeeadministration.model.Department
import com.example.employeeadministration.model.Employee
import com.example.employeeadministration.model.JobDetails
import com.example.employeeadministration.model.Position
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface JobDetailsRepository : JpaRepository<JobDetails, Long> {

    fun getByDepartmentAndPosition(department: Department, position: Position): Optional<JobDetails>
    fun getAllByDepartment(department: Department): List<JobDetails>
    fun getAllByPosition(position: Position): List<JobDetails>

}