package com.example.projectadministration.kafka

import com.example.projectadministration.model.employee.Department
import com.example.projectadministration.repositories.employeeservice.DepartmentRepository
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile
import org.springframework.transaction.UnexpectedRollbackException

@Configuration
@Profile("test")
class MockRepositoryConfig {

    @Bean
    @Primary
    fun mockDepartmentRepository(): DepartmentRepository {
        val mock = Mockito.mock(DepartmentRepository::class.java)
        Mockito.`when`(mock.save(Mockito.any(Department::class.java))).thenThrow(UnexpectedRollbackException::class.java)
        return mock
    }

}