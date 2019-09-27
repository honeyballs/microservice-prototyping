package com.example.projectadministration.kafka

import com.example.projectadministration.model.events.CompensatingAction
import com.example.projectadministration.model.events.CompensatingActionType
import com.example.projectadministration.model.events.DomainEvent
import com.example.projectadministration.repositories.employeeservice.PositionRepository
import com.example.projectadministration.services.kafka.EmployeeServiceKafkaEventHandler
import com.example.projectadministration.services.kafka.KafkaEventProducer
import org.assertj.core.api.Assertions
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestComponent
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import java.math.BigDecimal

@RunWith(SpringRunner::class)
@SpringBootTest
@DirtiesContext
@ActiveProfiles("test")
@EmbeddedKafka(partitions = 1, topics = [TOPIC_NAME])
class EventHandlingTests {

    @Autowired
    lateinit var producer: KafkaEventProducer

    @Autowired
    lateinit var consumer: EmployeeServiceKafkaEventHandler

    @Autowired
    lateinit var positionRepository: PositionRepository

    @Test
    fun shouldConvertToReduced() {
        val position = Position(12L, "Developer", BigDecimal(30.20), BigDecimal(40.50))
        val comp = PositionCreatedCompensation(12L)
        val event =  PositionCreatedEvent(position, comp)
        producer.sendDomainEvent(12L, event)
        Thread.sleep(1000)
        val createdPositions = positionRepository.findAll()
        Assertions.assertThat(createdPositions.size).isEqualTo(1)
        Assertions.assertThat(createdPositions[0].id).isEqualTo(12L)
    }





    // Create classes as they are in the employee service to check if the conversion to its reduced equivalent works
    class Position(val id: Long, val title: String, val minHourlyWage: BigDecimal, maxHourlyWage: BigDecimal, val deleted: Boolean = false)
    class PositionCreatedCompensation(val positionId: Long): CompensatingAction(CompensatingActionType.DELETE)
    class PositionCreatedEvent(val position: Position, compensatingAction: PositionCreatedCompensation): DomainEvent(compensatingAction)








}