package com.example.employeeadministration.kafka

import com.example.employeeadministration.configurations.TOPIC_NAME
import com.example.employeeadministration.model.Address
import com.example.employeeadministration.model.Employee
import com.example.employeeadministration.model.Position
import com.example.employeeadministration.model.PositionDto
import com.example.employeeadministration.model.events.CreatedPositionEvent
import com.example.employeeadministration.model.events.DomainEvent
import com.example.employeeadministration.model.events.PositionActions
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.IntegerDeserializer
import org.apache.kafka.common.serialization.IntegerSerializer
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.assertj.core.api.Assertions
import org.junit.After
import org.junit.AfterClass
import org.junit.ClassRule
import org.junit.Test
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

    private final val consumerConfigs = HashMap(KafkaTestUtils.consumerProps("employee", "false", embeddedKafka.embeddedKafka))
    val consumer = DefaultKafkaConsumerFactory<String, DomainEvent>(consumerConfigs, StringDeserializer(), JsonDeserializer<DomainEvent>(DomainEvent::class.java, false)).createConsumer()

    private final val producerConfig = HashMap(KafkaTestUtils.producerProps(embeddedKafka.embeddedKafka))
    val producer = DefaultKafkaProducerFactory<String, DomainEvent>(producerConfig, StringSerializer(), JsonSerializer<DomainEvent>()).createProducer()
    
    init {
        embeddedKafka.embeddedKafka.consumeFromAllEmbeddedTopics(consumer)
    }
    
    @Test
    fun employeeShouldBeSerialized() {
        val position = Position(12L, "Developer", BigDecimal(30.20), BigDecimal(47.213))
        val event = CreatedPositionEvent(position)
        producer.send(ProducerRecord(TOPIC_NAME, event.id, event))
        val message = KafkaTestUtils.getSingleRecord(consumer, TOPIC_NAME)
        println(message.value())
        Assertions.assertThat(UUID.fromString(message.key()).toString()).isEqualTo(event.id)
        Assertions.assertThat(message.value()).isEqualTo(event)
    }


}