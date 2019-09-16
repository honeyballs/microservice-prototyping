package com.example.employeeadministration.kafka

import com.example.employeeadministration.configurations.TOPIC_NAME
import com.example.employeeadministration.model.*
import com.example.employeeadministration.model.events.*
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.*
import org.assertj.core.api.Assertions
import org.junit.*
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.kafka.support.serializer.JsonSerializer
import org.springframework.kafka.test.EmbeddedKafkaBroker
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.kafka.test.rule.EmbeddedKafkaRule
import org.springframework.kafka.test.utils.KafkaTestUtils
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit4.SpringRunner
import java.math.BigDecimal
import java.security.DomainCombiner
import java.time.LocalDate
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.HashMap

@RunWith(SpringRunner::class)
@SpringBootTest
class KafkaTests {

    public companion object {
        @ClassRule
        @JvmField
        public val embeddedKafka = EmbeddedKafkaRule(1, true, TOPIC_NAME)
    }

    @Autowired
    lateinit var mapper: ObjectMapper

    private final val consumerConfigs = HashMap(KafkaTestUtils.consumerProps("employee", "false", embeddedKafka.embeddedKafka))

    lateinit var consumer: Consumer<Long, DomainEvent>

    private final val producerConfig = HashMap(KafkaTestUtils.producerProps(embeddedKafka.embeddedKafka))
    val producer = DefaultKafkaProducerFactory<Long, DomainEvent>(producerConfig, LongSerializer(), JsonSerializer()).createProducer()

    @Before
    fun setupConsumer() {
        consumer = DefaultKafkaConsumerFactory<Long, DomainEvent>(consumerConfigs, LongDeserializer(), JsonDeserializer(DomainEvent::class.java, mapper, true)).createConsumer()
        embeddedKafka.embeddedKafka.consumeFromAllEmbeddedTopics(consumer)
    }

//    @Test
//    fun shouldConvertToJsonCorrectly() {
//        val position = Position(12L, "Developer", BigDecimal(30.20), BigDecimal(47.213))
//        val comp = PositionCreatedCompensation(position.id!!)
//        val event = PositionCreatedEvent(position, comp)
//        val json = mapper.writeValueAsString(event)
//        val testEvent = mapper.readValue(json, PositionCreatedEvent::class.java)
//        println(json)
//        Assertions.assertThat(testEvent is PositionCreatedEvent).isTrue()
//    }

//    @Test
//    fun shouldBeAbleToCast() {
//        val position = Position(12L, "Developer", BigDecimal(30.20), BigDecimal(47.213))
//        val comp = PositionCreatedCompensation(position.id!!)
//        val event = PositionCreatedEvent(position, comp)
//        val domainEvent: DomainEvent = event as DomainEvent
//        Assertions.assertThat(domainEvent is PositionCreatedEvent).isTrue()
//    }

    @Test
    fun shouldSerializeCorrectly() {
//        val position = Position(12L, "Developer", BigDecimal(30.20), BigDecimal(47.213))
//        val comp = PositionCreatedCompensation(position.id!!)
//        val event = PositionCreatedEvent(position, comp)
        val department = Department(12L, "Development")
        val comp = DepartmentCreatedCompensation(12L)
        val event = DepartmentCreatedEvent(department, comp)
        producer.send(ProducerRecord(TOPIC_NAME, department.id!!, event))
        val message = KafkaTestUtils.getSingleRecord(consumer, TOPIC_NAME)
        val domainEvent: DomainEvent = message.value()
        Assertions.assertThat(domainEvent.id).isEqualTo(event.id)
        Assertions.assertThat(message.key()).isEqualTo(department.id!!)
        val testComp = (domainEvent as DepartmentCreatedEvent).compensatingAction as DepartmentCreatedCompensation
        Assertions.assertThat(testComp.departmentId).isEqualTo(department.id!!)
    }


}