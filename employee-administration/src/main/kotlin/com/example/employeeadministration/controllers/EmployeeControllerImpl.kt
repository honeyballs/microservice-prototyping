package com.example.employeeadministration.controllers

import com.example.employeeadministration.model.*
import com.example.employeeadministration.repositories.EmployeeRepository
import com.example.employeeadministration.repositories.JobDetailsRepository
import com.example.employeeadministration.services.EmployeeService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.math.BigDecimal

const val employeeUrl = "employees"

@RestController
class EmployeeControllerImpl(val repository: EmployeeRepository, val service: EmployeeService, val jobDetailsRepository: JobDetailsRepository) : EmployeeController {

    @GetMapping(employeeUrl)
    override fun getAllEmployees(): ResponseEntity<List<EmployeeDto>> {
        return ok(repository.findAll().map { service.mapEntityToDto(it) })
    }

    @GetMapping("$employeeUrl/{id}")
    override fun getEmployeeById(@PathVariable("id") id: Long): ResponseEntity<EmployeeDto> {
        return ok(repository.getById(id).map { service.mapEntityToDto(it) }.orElseThrow {
            ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not find employee using the given id")
        })
    }

    @GetMapping("$employeeUrl/department/{name}")
    override fun getEmployeesOfDepartment(@PathVariable("name") name: String): ResponseEntity<List<EmployeeDto>> {
        return ok(repository.getAllByJobDetails_Department(Department(name)).map { service.mapEntityToDto(it) })
    }

    @PostMapping("$employeeUrl/position")
    override fun getEmployeesByPosition(@RequestBody position: Position): ResponseEntity<List<EmployeeDto>> {
        return ok(repository.getAllByJobDetails_Position(position).map { service.mapEntityToDto(it) })
    }

    @GetMapping("$employeeUrl/name")
    override fun getEmployeesByName(@RequestParam("firstname") firstname: String, @RequestParam("lastname") lastname: String): ResponseEntity<List<EmployeeDto>> {
        return ok(repository.getAllByFirstnameContainingAndLastnameContaining(firstname, lastname).map { service.mapEntityToDto(it) })
    }

    @PostMapping(employeeUrl)
    override fun createEmployee(@RequestBody employeeDto: EmployeeDto): ResponseEntity<EmployeeDto> {
        val entity = repository.save(service.mapDtoToEntity(employeeDto))
        return ok(service.mapEntityToDto(entity))
    }

    @PutMapping(employeeUrl)
    override fun updateEmployee(@RequestBody employeeDto: EmployeeDto): ResponseEntity<EmployeeDto> {
        val entity = repository.save(service.mapDtoToEntity(employeeDto))
        return ok(service.mapEntityToDto(entity))
    }

    @DeleteMapping(employeeUrl)
    override fun deleteEmployee(@RequestBody employeeDto: EmployeeDto): ResponseEntity<EmployeeDto> {
        repository.delete(service.mapDtoToEntity(employeeDto))
        return ok(employeeDto)
    }

    @GetMapping("$employeeUrl/actions/{id}/namechange")
    override fun employeeChangesName(@PathVariable("id") id: Long, @RequestParam("firstname") firstname: String, @RequestParam("lastname") lastname: String): ResponseEntity<EmployeeDto> {
        val employee = savelyRetrieveEmployee(id)
        employee.changeName(firstname, lastname)
        return ok(service.mapEntityToDto(repository.save(employee)))
    }

    @PostMapping("$employeeUrl/actions/{id}/move")
    override fun employeeMoves(@PathVariable("id") id: Long, @RequestBody() address: Address): ResponseEntity<EmployeeDto> {
        val employee = savelyRetrieveEmployee(id)
        employee.moveToNewAddress(address)
        return ok(service.mapEntityToDto(repository.save(employee)))
    }

    @PostMapping("$employeeUrl/actions/{id}/switchbank")
    override fun employeeSwitchesBankDetails(@PathVariable("id") id: Long, @RequestBody details: BankDetails): ResponseEntity<EmployeeDto> {
        val employee = savelyRetrieveEmployee(id)
        employee.switchBankDetails(details)
        return ok(service.mapEntityToDto(repository.save(employee)))
    }

    @GetMapping("$employeeUrl/actions/{id}/raise")
    override fun employeeReceivesRaise(@PathVariable("id") id: Long, @RequestParam("amount") amount: BigDecimal): ResponseEntity<EmployeeDto> {
        val employee = savelyRetrieveEmployee(id)
        employee.receiveRaiseBy(amount)
        return ok(service.mapEntityToDto(repository.save(employee)))
    }

    @GetMapping("$employeeUrl/actions/{id}/changedepartment")
    override fun employeeMovesToDepartment(@PathVariable("id") id: Long, @RequestParam("name") name: String): ResponseEntity<EmployeeDto> {
        val employee = savelyRetrieveEmployee(id)
        val details = jobDetailsRepository.getByDepartmentAndPosition(Department(name), employee.jobDetails.position).orElseThrow {
            ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not find job a department/position combination fitting the requested move")
        }
        employee.moveToAnotherDepartment(details)
        return ok(service.mapEntityToDto(repository.save(employee)))
    }

    @PostMapping("$employeeUrl/actions/{id}/newposition")
    override fun employeeReceivesNewPosition(@PathVariable("id") id: Long, @RequestBody position: Position, @RequestParam("salary") newSalary: BigDecimal): ResponseEntity<EmployeeDto> {
        val employee = savelyRetrieveEmployee(id)
        val details = jobDetailsRepository.getByDepartmentAndPosition(employee.jobDetails.department, position).orElseThrow {
            ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not find job a department/position combination fitting the requested move")
        }
        employee.changeJobPosition(details, newSalary)
        return ok(service.mapEntityToDto(repository.save(employee)))
    }

    fun savelyRetrieveEmployee(id: Long): Employee {
        return repository.getById(id).orElseThrow {
            ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not find employee using the given id")
        }
    }
}