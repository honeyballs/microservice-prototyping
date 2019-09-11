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

@RunWith(SpringRunner::class)
@SpringBootTest
@Transactional
class EmployeeRepoTests {

    @Autowired
    lateinit var employeeRepository: EmployeeRepository

    @Autowired
    lateinit var departmentRepository: DepartmentRepository

    @Autowired
    lateinit var positionRepository: PositionRepository

    val department = Department(null, "Java Development")
    val department2 = Department(null, "C# Development")
    val position = Position(null, "Junior Consultant", BigDecimal(25.00), BigDecimal(40.20))
    val address = Address("Teststr.", 17, "Berlin", ZipCode(12345))
    val bankDetails = BankDetails("128319815719", "4712841", "Sparkasse")

    @Before
    fun saveJobDetailsAndEmployee() {
        departmentRepository.saveAll(listOf(department, department2))
        positionRepository.save(position)
        val employee = Employee(null, "Max", "Mustermann", LocalDate.now().minusYears(20), address, bankDetails, department, position, BigDecimal(35.70), null)
        val differentEmployee = Employee(null, "Test", "Testerson", LocalDate.now().minusYears(20), address, bankDetails, department2, position, BigDecimal(37.70), null)
        employeeRepository.saveAll(listOf(employee, differentEmployee))
    }

    @Test
    fun employeeShouldBeSaved() {
        Assertions.assertThat(employeeRepository.findAll().size).isEqualTo(2)
    }

    @Test
    fun shouldFindByDepartment() {
        val list = employeeRepository.getAllByDepartment_Id(department.id!!)
        Assertions.assertThat(list.size).isEqualTo(1)
        Assertions.assertThat(list[0].firstname).isEqualTo("Max")
    }

    @Test
    fun shouldFindByPosition() {
        Assertions.assertThat(employeeRepository.getAllByPosition_Id(position.id!!).size).isEqualTo(2)
    }

    @Test
    fun shouldFindByPartOfLastname() {
        Assertions.assertThat(employeeRepository.getAllByFirstnameContainingAndLastnameContaining("", "usterma").size).isEqualTo(1)
    }

}