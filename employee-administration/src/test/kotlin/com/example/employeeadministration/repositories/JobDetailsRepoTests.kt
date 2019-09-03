package com.example.employeeadministration.repositories

import com.example.employeeadministration.model.Department
import com.example.employeeadministration.model.JobDetails
import com.example.employeeadministration.model.Position
import org.assertj.core.api.Assertions
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@RunWith(SpringRunner::class)
@SpringBootTest
@Transactional
class JobDetailsRepoTests {

    @Autowired
    lateinit var jobDetailsRepository: JobDetailsRepository

    val department = Department("Java Development")
    val position = Position("Junior Consultant", BigDecimal(25.00), BigDecimal(40.20))

    @Before
    fun saveOneDetail() {
        jobDetailsRepository.save(JobDetails(null, department, position))
    }

    @Test
    fun findAllShouldContainOne() {
        Assertions.assertThat(jobDetailsRepository.findAll().size).isEqualTo(1)
    }

    @Test
    fun shouldFilterCorrectly() {
        val newDepartment = Department("C# Development")
        val differentDetails = JobDetails(null, newDepartment, position)
        jobDetailsRepository.save(differentDetails)
        Assertions.assertThat(jobDetailsRepository.getByDepartmentAndPosition(newDepartment, position).isPresent).isTrue()
        Assertions.assertThat(jobDetailsRepository.getAllByDepartment(newDepartment).size).isEqualTo(1)
        Assertions.assertThat(jobDetailsRepository.getAllByPosition(position).size).isEqualTo(2)
    }


}