package com.example.employeeadministration.controllers

import com.example.employeeadministration.model.Department
import com.example.employeeadministration.model.JobDetails
import com.example.employeeadministration.model.JobDetailsDto
import com.example.employeeadministration.model.Position
import org.springframework.boot.autoconfigure.batch.BatchProperties
import org.springframework.http.ResponseEntity

interface JobDetailsController {

    fun getAllJobDetails(): ResponseEntity<List<JobDetailsDto>>
    fun getJobDetailsById(id: Long): ResponseEntity<JobDetailsDto>
    fun getAllJobDetailsByDepartment(name: String): ResponseEntity<List<JobDetailsDto>>
    fun getAllJobDetailsByPosition(position: Position): ResponseEntity<List<JobDetailsDto>>

    fun createJobDetails(jobDetailsDto: JobDetailsDto): ResponseEntity<JobDetailsDto>
    fun updateJobDetails(jobDetailsDto: JobDetailsDto): ResponseEntity<JobDetailsDto>
    fun deleteJobDetails(jobDetailsDto: JobDetailsDto): ResponseEntity<JobDetailsDto>

}