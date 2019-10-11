package com.example.employeeadministration.kafka

import com.example.employeeadministration.model.aggregates.DEPARTMENT_AGGREGATE_NAME
import com.example.employeeadministration.model.aggregates.EMPLOYEE_AGGREGATE_NAME
import com.example.employeeadministration.model.aggregates.POSITION_AGGREGATE_NAME
import com.example.employeeadministration.model.events.*
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.common.serialization.*
import org.junit.*
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.kafka.support.serializer.JsonSerializer
import org.springframework.kafka.test.rule.EmbeddedKafkaRule
import org.springframework.kafka.test.utils.KafkaTestUtils
import org.springframework.test.context.junit4.SpringRunner
import kotlin.collections.HashMap

@RunWith(SpringRunner::class)
@SpringBootTest
class KafkaTests {

    public companion object {
        @ClassRule
        @JvmField
        public val embeddedKafka = EmbeddedKafkaRule(1, true, DEPARTMENT_AGGREGATE_NAME, EMPLOYEE_AGGREGATE_NAME, POSITION_AGGREGATE_NAME)
    }

    @Autowired
    lateinit var mapper: ObjectMapper

    private final val consumerConfigs = HashMap(KafkaTestUtils.consumerProps("employee", "false", embeddedKafka.embeddedKafka))

    lateinit var consumer: Consumer<Long, Event>

    private final val producerConfig = HashMap(KafkaTestUtils.producerProps(embeddedKafka.embeddedKafka))
    val producer = DefaultKafkaProducerFactory<Long, Event>(producerConfig, LongSerializer(), JsonSerializer()).createProducer()

    @Before
    fun setupConsumer() {
        consumer = DefaultKafkaConsumerFactory<Long, Event>(consumerConfigs, LongDeserializer(), JsonDeserializer(Event::class.java, mapper, true)).createConsumer()
        embeddedKafka.embeddedKafka.consumeFromAllEmbeddedTopics(consumer)
    }

//    @Test
//    fun shouldSerializeCorrectly() {
//        val department = Department(12L, "Development")
//        val comp = DepartmentCreatedCompensation(12L)
//        val event = DepartmentCreatedEvent(department.id!!, department.name, comp)
//        producer.send(ProducerRecord(TOPIC_NAME, department.id!!, event))
//        val message = KafkaTestUtils.getSingleRecord(consumer, TOPIC_NAME)
//        val domainEvent: DomainEvent = message.value() as DomainEvent
//        Assertions.assertThat(domainEvent.id).isEqualTo(event.id)
//        Assertions.assertThat(message.key()).isEqualTo(department.id!!)
//        val testComp = (domainEvent as DepartmentCreatedEvent).compensatingAction as DepartmentCreatedCompensation
//        Assertions.assertThat(testComp.departmentId).isEqualTo(department.id!!)
//    }

//    @Test
//    fun shouldAlsoAcceptCompensatingActions() {
//        val comp = DepartmentCreatedCompensation(12L)
//        comp.originalEventId = UUID.randomUUID().toString()
//        comp.eventCreatedAt = LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN))
//        producer.send(ProducerRecord(TOPIC_NAME, comp.departmentId, comp))
//        val message = KafkaTestUtils.getSingleRecord(consumer, TOPIC_NAME)
//        Assertions.assertThat(message.value() is CompensatingAction).isTrue()
//        Assertions.assertThat(message.value() is DepartmentCreatedCompensation).isTrue()
//    }


}