package com.example.employeeadministration.services

import com.example.employeeadministration.model.Department
import com.example.employeeadministration.model.JobDetails
import com.example.employeeadministration.model.Position
import com.example.employeeadministration.repositories.JobDetailsRepository
import org.springframework.stereotype.Service

@Service
class JobDetailsServiceProd(val jobDetailsRepository: JobDetailsRepository) : JobDetailsService {

    override fun uniquelySaveJobDetails(details: JobDetails): JobDetails {
        return jobDetailsRepository.findByDepartmentAndPosition(details.department, details.position).orElse(jobDetailsRepository.save(details))
    }

    override fun addPositionToDepartment(department: Department, position: Position): JobDetails {
        val jobDetails = JobDetails(null, department, position)
        return uniquelySaveJobDetails(jobDetails)
    }

}