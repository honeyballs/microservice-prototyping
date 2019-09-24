package com.example.employeeadministration.kafka

import com.example.employeeadministration.model.Department
import com.example.employeeadministration.model.events.DepartmentCreatedCompensation
import com.example.employeeadministration.repositories.DepartmentRepository
import com.example.employeeadministration.services.kafka.KafkaEventHandler
import com.example.employeeadministration.services.kafka.KafkaEventProducer
import org.assertj.core.api.Assertions
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.annotation.KafkaHandler
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest
@DirtiesContext
@ActiveProfiles("test")
@EmbeddedKafka(partitions = 1, topics = [TOPIC_NAME])
class KafkaApplicationTests {

    @Autowired
    lateinit var producer: KafkaEventProducer

    @Autowired
    lateinit var consumer: KafkaEventHandler

    @Autowired
    lateinit var departmentRepository: DepartmentRepository

    lateinit var department: Department

    @Before
    fun setup() {
        department = Department(null, "Development")
        department = departmentRepository.save(department)
    }


    @Test
    fun shouldSendCompensation() {
        val comp = DepartmentCreatedCompensation(department.id!!)
        producer.sendDomainEvent(department.id!!, comp)
        Thread.sleep(1000)
        val deletedDepartment = departmentRepository.findById(department.id!!).get()
        Assertions.assertThat(deletedDepartment.deleted).isTrue()
    }



}