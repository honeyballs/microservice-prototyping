package com.example.employeeadministration.configurations

import com.example.employeeadministration.model.aggregates.DEPARTMENT_AGGREGATE_NAME
import com.example.employeeadministration.model.aggregates.EMPLOYEE_AGGREGATE_NAME
import com.example.employeeadministration.model.aggregates.POSITION_AGGREGATE_NAME
import com.example.employeeadministration.model.events.Event
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.LongSerializer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.core.env.Environment
import org.springframework.kafka.core.*
import org.springframework.kafka.support.serializer.JsonSerializer
import org.springframework.kafka.transaction.KafkaTransactionManager
import org.xerial.snappy.buffer.DefaultBufferAllocator.factory

@Configuration
@Profile("!test")
class KafkaConfiguration {

    // We have to use the spring configured object mapper which is able to map kotlin
    @Autowired
    lateinit var mapper: ObjectMapper

    @Autowired
    lateinit var env: Environment

    @Bean
    fun employeeTopic(): NewTopic {
        return NewTopic(EMPLOYEE_AGGREGATE_NAME, 1, 1)
    }

    @Bean
    fun departmentTopic(): NewTopic {
        return NewTopic(DEPARTMENT_AGGREGATE_NAME, 1, 1)
    }

    @Bean
    fun positionTopic(): NewTopic {
        return NewTopic(POSITION_AGGREGATE_NAME, 1, 1)
    }

    @Bean
    fun producerConfigs():Map<String, Any> {
        val configs = HashMap<String, Any>()
        configs[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = env.getProperty("KAFKA_URL", "localhost:9093")
        return configs
    }

    @Bean
    fun admin(): KafkaAdmin {
        return KafkaAdmin(producerConfigs())
    }

    @Bean
    fun producerFactory(): ProducerFactory<Long, Event> {
        val serializer = JsonSerializer<Event>(mapper)
        serializer.isAddTypeInfo = false
        val factory = DefaultKafkaProducerFactory<Long, Event>(producerConfigs(), LongSerializer(), serializer)
        factory.setTransactionIdPrefix("employee-administration")
        return factory
    }

    // Provides another consumer factory using a transaction prefix per instance, not for all application instances
    // This is required when messages are only produced:
    // See https://docs.spring.io/spring-kafka/reference/html/#transaction-id-prefix
    @Bean
    fun producingOnlyProducerFactory(): ProducerFactory<Long, Event> {
        val serializer = JsonSerializer<Event>(mapper)
        serializer.isAddTypeInfo = false
        val factory = DefaultKafkaProducerFactory<Long, Event>(producerConfigs(), LongSerializer(), serializer)
        val containerNr = env.getProperty("CONTAINER_NR", "")
        factory.setTransactionIdPrefix("employee-administration${containerNrWithDot(containerNr)}")
        return factory
    }

    @Bean
    fun kafkaTransactionManager(producerFactory: ProducerFactory<Long, Event>): KafkaTransactionManager<Long, Event> {
        return KafkaTransactionManager(producerFactory)
    }

    @Bean
    fun kafkaTemplate(producerFactory: ProducerFactory<Long, Event>): KafkaTemplate<Long, Event> {
        return KafkaTemplate<Long, Event>(producerFactory)
    }

    @Bean
    fun producingOnlyTemplate(producingOnlyProducerFactory: ProducerFactory<Long, Event>): KafkaTemplate<Long, Event> {
        return KafkaTemplate<Long, Event>(producingOnlyProducerFactory)
    }

    fun containerNrWithDot(nr: String): String {
        if (nr != "") {
            return ".$nr"
        }
        return nr
    }
}