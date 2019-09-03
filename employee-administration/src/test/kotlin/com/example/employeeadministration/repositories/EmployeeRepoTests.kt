package com.example.employeeadministration.repositories

import com.example.employeeadministration.model.*
import org.assertj.core.api.Assertions
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*

@RunWith(SpringRunner::class)
@SpringBootTest
@Transactional
class EmployeeRepoTests {

    @Autowired
    lateinit var jobDetailsRepository: JobDetailsRepository

    @Autowired
    lateinit var employeeRepository: EmployeeRepository

    val department = Department("Java Development")
    val department2 = Department("C# Development")
    val position = Position("Junior Consultant", BigDecimal(25.00), BigDecimal(40.20))
    val address = Address("Teststr.", 17, "Berlin", ZipCode(12345))
    val bankDetails = BankDetails("128319815719", "4712841", "Sparkasse")

    var jobDetails = JobDetails(null, department, position)
    var differentDetails = JobDetails(null, department2, position)

    @Before
    fun saveJobDetailsAndEmployee() {
        jobDetailsRepository.saveAll(listOf(jobDetails, differentDetails))
        val employee = Employee(null, "Max", "Mustermann", LocalDate.now().minusYears(20), address, bankDetails, jobDetails, BigDecimal(35.70), null)
        val differentEmployee = Employee(null, "Test", "Testerson", LocalDate.now().minusYears(20), address, bankDetails, differentDetails, BigDecimal(37.70), null)
        employeeRepository.saveAll(listOf(employee, differentEmployee))
    }

    @Test
    fun employeeShouldBeSaved() {
        Assertions.assertThat(employeeRepository.findAll().size).isEqualTo(2)
    }

    @Test
    fun shouldFindByDepartment() {
        val list = employeeRepository.getAllByJobDetails_Department(department)
        Assertions.assertThat(list.size).isEqualTo(1)
        Assertions.assertThat(list[0].firstname).isEqualTo("Max")
    }

    @Test
    fun shouldFindByPosition() {
        Assertions.assertThat(employeeRepository.getAllByJobDetails_Position(position).size).isEqualTo(2)
    }

    @Test
    fun shouldFindByPartOfLastname() {
        Assertions.assertThat(employeeRepository.getAllByFirstnameContainingAndLastnameContaining("", "usterma").size).isEqualTo(1)
    }

}