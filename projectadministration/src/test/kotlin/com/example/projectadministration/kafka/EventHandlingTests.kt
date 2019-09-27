package com.example.projectadministration.kafka

import com.example.projectadministration.model.employee.Department
import com.example.projectadministration.model.events.*
import com.example.projectadministration.repositories.employeeservice.DepartmentRepository
import com.example.projectadministration.repositories.employeeservice.PositionRepository
import com.example.projectadministration.services.kafka.EmployeeServiceKafkaEventHandler
import com.example.projectadministration.services.kafka.KafkaEventProducer
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.common.serialization.LongDeserializer
import org.assertj.core.api.Assertions
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestComponent
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.kafka.test.EmbeddedKafkaBroker
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.kafka.test.utils.KafkaTestUtils
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.transaction.UnexpectedRollbackException
import java.math.BigDecimal

@RunWith(SpringRunner::class)
@SpringBootTest
@DirtiesContext
@ActiveProfiles("test")
@EmbeddedKafka(partitions = 1, topics = [TOPIC_NAME])
class EventHandlingTests {

    @Autowired
    lateinit var mapper: ObjectMapper

    @Autowired
    lateinit var producer: KafkaEventProducer

    @Autowired
    lateinit var consumer: EmployeeServiceKafkaEventHandler

    @Autowired
    lateinit var positionRepository: PositionRepository


    @Test
    fun shouldSaveOwnPositionInDB() {
        val comp = PositionCreatedCompensation(12L)
        val event =  PositionCreatedEvent(12L, "Developer", comp)
        producer.sendDomainEvent(12L, event)
        Thread.sleep(1000)
        val createdPositions = positionRepository.findAll()
        Assertions.assertThat(createdPositions.size).isEqualTo(1)
        Assertions.assertThat(createdPositions[0].id).isEqualTo(12L)
    }

}