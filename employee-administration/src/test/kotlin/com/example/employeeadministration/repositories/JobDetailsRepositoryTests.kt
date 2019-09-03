package com.example.employeeadministration.repositories

import com.example.employeeadministration.model.Department
import com.example.employeeadministration.model.JobDetails
import com.example.employeeadministration.model.Position
import org.assertj.core.api.Assertions
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestComponent
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringRunner
import java.math.BigDecimal

@RunWith(SpringRunner::class)
@SpringBootTest
class JobDetailsRepositoryTests {

    @Autowired
    lateinit var jobDetailsRepository: JobDetailsRepository

    val position = Position("Senior Consultant", BigDecimal(50.00), BigDecimal(75.00))
    val department = Department("Development")


    @Test
    fun shouldSaveJobDetails() {
        val jobDetails = JobDetails(null, department, position)
        jobDetailsRepository.save(jobDetails)
        Assertions.assertThat(jobDetailsRepository.findAll().size).isEqualTo(1)
    }

    @Test
    fun shouldGetJobDetailsByProperties() {
        Assertions.assertThat(jobDetailsRepository.findByDepartmentAndPosition(department, position).isPresent).isTrue()
    }

    @Test
    fun shouldGetJobDetailsByDepartment() {
        val newDetails = JobDetails(null, department, Position("Junior Consultant", BigDecimal(30.00), BigDecimal(45.20)))
        jobDetailsRepository.save(newDetails)
        Assertions.assertThat(jobDetailsRepository.findDistinctByDepartment(department).size).isEqualTo(2)
    }

}