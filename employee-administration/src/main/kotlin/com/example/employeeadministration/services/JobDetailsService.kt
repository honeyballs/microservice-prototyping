package com.example.employeeadministration.services

import com.example.employeeadministration.model.Department
import com.example.employeeadministration.model.JobDetails
import com.example.employeeadministration.model.JobDetailsDto
import com.example.employeeadministration.model.Position

interface JobDetailsService : MappingService<JobDetails, JobDetailsDto> {

    fun uniquelySaveJobDetails(details: JobDetails): JobDetailsDto
    fun addPositionToDepartment(department: Department, position: Position): JobDetailsDto

}