package com.example.employeeadministration.repositories

import com.example.employeeadministration.model.Department
import com.example.employeeadministration.model.Employee
import com.example.employeeadministration.model.JobDetails
import com.example.employeeadministration.model.Position
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import java.util.*

interface JobDetailsRepository : MongoRepository <JobDetails, String>  {

    fun findByDepartmentAndPosition(department: Department, position: Position): Optional<JobDetails>
    fun findDistinctByDepartment(department: Department): List<JobDetails>
    fun findDistinctByPosition(position: Position): List<JobDetails>

}