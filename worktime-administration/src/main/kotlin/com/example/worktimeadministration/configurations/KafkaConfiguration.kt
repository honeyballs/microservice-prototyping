package com.example.worktimeadministration.configurations

import com.example.worktimeadministration.model.aggregates.WORKTIME_AGGREGATE_NAME
import com.example.worktimeadministration.model.aggregates.employee.EMPLOYEE_AGGREGATE_NAME
import com.example.worktimeadministration.model.aggregates.project.PROJECT_AGGREGATE_NAME
import com.example.worktimeadministration.model.events.Event
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.LongSerializer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.core.env.Environment
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.core.*
import org.springframework.kafka.support.serializer.JsonSerializer

const val TOPIC_NAME = "employee"

@Configuration
@EnableKafka
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
    fun projectTopic(): NewTopic {
        return NewTopic(PROJECT_AGGREGATE_NAME, 1, 1)
    }

    @Bean
    fun worktimeTopic(): NewTopic {
        return NewTopic(WORKTIME_AGGREGATE_NAME, 1, 1)
    }

    @Bean
    fun producerConfigs():Map<String, Any> {
        val configs = HashMap<String, Any>()
        configs[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = env.getProperty("KAFKA_URL", "localhost:9092")
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
        return DefaultKafkaProducerFactory<Long, Event>(producerConfigs(), LongSerializer(), serializer)
    }

    @Bean
    fun kafkaTemplate(): KafkaTemplate<Long, Event> {
        return KafkaTemplate<Long, Event>(producerFactory())
    }

}