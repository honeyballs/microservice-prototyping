package com.example.projectadministration.kafka

import com.example.projectadministration.model.employee.Position
import com.example.projectadministration.model.events.EventType
import com.example.projectadministration.model.events.PositionCompensation
import com.example.projectadministration.model.events.PositionEvent
import com.example.projectadministration.repositories.employeeservice.PositionRepository
import com.example.projectadministration.services.kafka.KafkaEventProducer
import com.example.projectadministration.services.kafka.employee.EmployeeServiceDepartmentKafkaEventHandler
import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner

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
    lateinit var departmentConsumer: EmployeeServiceDepartmentKafkaEventHandler

    @Autowired
    lateinit var positionRepository: PositionRepository


    @Test
    fun shouldSaveOwnPositionInDB() {
        val position = Position(null, 12L, "Developer")
        val comp = PositionCompensation(position, EventType.CREATE)
        val event =  PositionEvent(position, comp, EventType.CREATE)
        producer.sendDomainEvent(12L, event)
        Thread.sleep(1000)
        val createdPositions = positionRepository.findAll()
        Assertions.assertThat(createdPositions.size).isEqualTo(1)
        Assertions.assertThat(createdPositions[0].id).isEqualTo(12L)
    }

}