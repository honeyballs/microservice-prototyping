package com.example.employeeadministration.controllers

import com.example.employeeadministration.model.Department
import com.example.employeeadministration.model.JobDetailsDto
import com.example.employeeadministration.model.Position
import com.example.employeeadministration.repositories.JobDetailsRepository
import com.example.employeeadministration.services.JobDetailsService
import javassist.tools.web.BadHttpRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.server.ResponseStatusException
import java.lang.Exception
import java.net.http.HttpClient

const val jobDetailsUrl = "jobdetails"

@RestController
class JobDetailsControllerImpl(val repository: JobDetailsRepository, val service: JobDetailsService) : JobDetailsController{

    @GetMapping(jobDetailsUrl)
    override fun getAllJobDetails(): ResponseEntity<List<JobDetailsDto>> {
        return ok(repository.findAll().map { service.mapEntityToDto(it) })
    }

    @GetMapping("$jobDetailsUrl/{id}")
    override fun getJobDetailsById(@PathVariable("id") id: Long): ResponseEntity<JobDetailsDto> {
        return ok(repository.getById(id).map { service.mapEntityToDto(it) }.orElseThrow {
            ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not find jobdetails using the given id")
        })
    }

    @GetMapping("$jobDetailsUrl/department/{name}")
    override fun getAllJobDetailsByDepartment(@PathVariable("name") name: String): ResponseEntity<List<JobDetailsDto>> {
        return ok(repository.getAllByDepartment(Department(name)).map { service.mapEntityToDto(it) })
    }

    @PostMapping("$jobDetailsUrl/position")
    override fun getAllJobDetailsByPosition(@RequestBody position: Position): ResponseEntity<List<JobDetailsDto>> {
        return ok(repository.getAllByPosition(position).map { service.mapEntityToDto(it) })
    }

    @PostMapping(jobDetailsUrl)
    override fun createJobDetails(@RequestBody jobDetailsDto: JobDetailsDto): ResponseEntity<JobDetailsDto> {
        return ok(service.uniquelySaveJobDetails((service.mapDtoToEntity(jobDetailsDto))))
    }

    @PutMapping(jobDetailsUrl)
    override fun updateJobDetails(@RequestBody jobDetailsDto: JobDetailsDto): ResponseEntity<JobDetailsDto> {
        return ok(service.uniquelySaveJobDetails((service.mapDtoToEntity(jobDetailsDto))))
    }

    @DeleteMapping(jobDetailsUrl)
    override fun deleteJobDetails(@RequestBody jobDetailsDto: JobDetailsDto): ResponseEntity<JobDetailsDto> {
        repository.delete(service.mapDtoToEntity(jobDetailsDto))
        return ok(jobDetailsDto)
    }

}