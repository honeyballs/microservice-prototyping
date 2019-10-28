package com.example.employeeadministration.controllers

import com.example.employeeadministration.model.aggregates.Employee
import com.example.employeeadministration.model.dto.EmployeeDto
import com.example.employeeadministration.model.valueobjects.Address
import com.example.employeeadministration.model.valueobjects.BankDetails
import com.example.employeeadministration.repositories.DepartmentRepository
import com.example.employeeadministration.repositories.EmployeeRepository
import com.example.employeeadministration.repositories.PositionRepository
import com.example.employeeadministration.services.EmployeeService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.math.BigDecimal

const val employeeUrl = "employees"

@RestController
class EmployeeControllerImpl(val repository: EmployeeRepository, val service: EmployeeService, val departmentRepository: DepartmentRepository, val positionRepository: PositionRepository) : EmployeeController {

    @GetMapping(employeeUrl)
    override fun getAllEmployees(): ResponseEntity<List<EmployeeDto>> {
        return ok(repository.getAllByDeletedFalse().map { service.mapEntityToDto(it) })
    }

    @GetMapping("$employeeUrl/{id}")
    override fun getEmployeeById(@PathVariable("id") id: Long): ResponseEntity<EmployeeDto> {
        return ok(repository.getByIdAndDeletedFalse(id).map { service.mapEntityToDto(it) }.orElseThrow {
            ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not find employee using the given id")
        })
    }

    @GetMapping("$employeeUrl/department/{depId}")
    override fun getEmployeesOfDepartment(@PathVariable("depId") departmentId: Long): ResponseEntity<List<EmployeeDto>> {
        return ok(repository.getAllByDepartment_IdAndDeletedFalse(departmentId).map { service.mapEntityToDto(it) })
    }

    @PostMapping("$employeeUrl/position/{posId}")
    override fun getEmployeesByPosition(@PathVariable("posId") positionId: Long): ResponseEntity<List<EmployeeDto>> {
        return ok(repository.getAllByPosition_IdAndDeletedFalse(positionId).map { service.mapEntityToDto(it) })
    }

    @GetMapping("$employeeUrl/name")
    override fun getEmployeesByName(@RequestParam("firstname") firstname: String, @RequestParam("lastname") lastname: String): ResponseEntity<List<EmployeeDto>> {
        return ok(repository.getAllByFirstnameContainingAndLastnameContainingAndDeletedFalse(firstname, lastname).map { service.mapEntityToDto(it) })
    }

    @PostMapping(employeeUrl)
    override fun createEmployee(@RequestBody employeeDto: EmployeeDto): ResponseEntity<EmployeeDto> {
        val entity = service.persistWithEvents(service.mapDtoToEntity(employeeDto))
        return ok(service.mapEntityToDto(entity))
    }

    @PutMapping(employeeUrl)
    override fun updateEmployee(@RequestBody employeeDto: EmployeeDto): ResponseEntity<EmployeeDto> {
        try {
            return ok(service.updateEmployee(employeeDto))
        } catch (ex: Exception) {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong when updating the employee", ex)
        }
    }

    @DeleteMapping("$employeeUrl/{id}")
    override fun deleteEmployee(@PathVariable("id") id: Long) {
        try {
            service.deleteEmployee(id)
        } catch (e: Exception) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message)
        }
    }

    fun savelyRetrieveEmployee(id: Long): Employee {
        return repository.getByIdAndDeletedFalse(id).orElseThrow {
            ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not find employee using the given id")
        }
    }
}